package com.lovbe.icharge.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.config.ServiceProperties;
import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.dto.AccountDo;
import com.lovbe.icharge.common.model.dto.SimpleCodeReqDTO;
import com.lovbe.icharge.common.model.dto.VCodeTemplateDO;
import com.lovbe.icharge.common.model.vo.BindingCodeReqVo;
import com.lovbe.icharge.common.model.vo.EmailCodeReqVo;
import com.lovbe.icharge.common.model.vo.SmsCodeReqVo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.common.util.servlet.ServletUtils;
import com.lovbe.icharge.dao.CodeLogMapper;
import com.lovbe.icharge.entity.dto.CodeLogDo;
import com.lovbe.icharge.entity.dto.CodeSendDTO;
import com.lovbe.icharge.entity.dto.SimpleSendResultDTO;
import com.lovbe.icharge.dao.AccountMapper;
import com.lovbe.icharge.dao.SimpleCodeMapper;
import com.lovbe.icharge.service.SimpleCodeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lovbe.icharge.common.util.CommonUtils.bitwiseInvert;

@Slf4j
@Service
@Valid
public class SimpleCodeServiceImpl implements SimpleCodeService {
    @Resource
    private SimpleCodeMapper simpleCodeMapper;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private CodeLogMapper codeLogMapper;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private CommonService commonService;
    @Value("${spring.kafka.topics.send-code}")
    private String sendCodeTopic;
    @Value("${spring.application.name}")
    private String appName;
    @Resource
    private ServiceProperties properties;

    private final HttpClient httpClient;

    public SimpleCodeServiceImpl() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public void sendSmsCode(SmsCodeReqVo reqVo, Long userId) {
        SimpleCodeReqDTO codeReqDTO = new SimpleCodeReqDTO()
                .setMobile(reqVo.getMobile())
                .setScene(reqVo.getScene())
                .setSign(reqVo.getSign())
                .setUserId(userId)
                .setUsedIp(ServletUtils.getClientIP());
        // 判断使用场景是否合法
        checkCodeSceneValidate(codeReqDTO);
        // 判断能否创建验证码
        String code = canCreateCode(codeReqDTO);
        codeReqDTO.setCode(code);
        // 发送短信验证码
        createSendLog(codeReqDTO);
    }

    @Override
    public void sendEmailCode(EmailCodeReqVo reqVo, Long userId) {
        // 判断并创建验证码
        SimpleCodeReqDTO codeReqDTO = new SimpleCodeReqDTO()
                .setEmail(reqVo.getEmail())
                .setScene(reqVo.getScene().getScene())
                .setSign(reqVo.getSign())
                .setUserId(userId)
                .setUsedIp(ServletUtils.getClientIP());
        // 判断使用场景是否合法
        checkCodeSceneValidate(codeReqDTO);
        // 判断能否创建验证码
        String code = canCreateCode(codeReqDTO);
        codeReqDTO.setCode(code);
        // 发送短信验证码
        createSendLog(codeReqDTO);
    }

    @Override
    public void useVerifyCode(SimpleCodeReqDTO reqDTO, Long userId) throws ServiceException {
        // 业务参数校验
        Integer scene = reqDTO.getScene();
        if (CodeSceneEnum.sceneMustLogin(scene)) {
            AccountDo accountDo = accountMapper.selectById(userId);
            Assert.isTrue(accountDo != null && CommonStatusEnum.isNormal(accountDo.getStatus()), ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR.getMsg());
            reqDTO.setUserId(userId);
            if (Objects.equals(CodeSceneEnum.BIND_EMAIL.getScene(), reqDTO.getScene()) ||
                    Objects.equals(CodeSceneEnum.MOBILE_UPDATE_MOBILE.getScene(), reqDTO.getScene()) ||
                    Objects.equals(CodeSceneEnum.MOBILE_UPDATE_EMAIL.getScene(), reqDTO.getScene()) ||
                    Objects.equals(CodeSceneEnum.MOBILE_UPDATE_PASSWORD.getScene(), reqDTO.getScene())) {
                reqDTO.setMobile(CommonUtils.decryptStr(accountDo.getMobile()));
            } else if (Objects.equals(CodeSceneEnum.BIND_MOBILE.getScene(), reqDTO.getScene()) ||
                    Objects.equals(CodeSceneEnum.EMAIL_UPDATE_MOBILE.getScene(), reqDTO.getScene()) ||
                    Objects.equals(CodeSceneEnum.EMAIL_UPDATE_EMAIL.getScene(), reqDTO.getScene()) ||
                    Objects.equals(CodeSceneEnum.EMAIL_UPDATE_PASSWORD.getScene(), reqDTO.getScene())) {
                reqDTO.setEmail(CommonUtils.decryptStr(accountDo.getEmail()));
            }
        } else if (CodeSceneEnum.sceneIsMobile(scene)) {
            Assert.notNull(reqDTO.getMobile(), SysConstant.NOT_EMPTY_MOBILE);
        } else {
            Assert.notNull(reqDTO.getEmail(), SysConstant.NOT_EMPTY_EMAIL);
        }

        // 记录验证码使用次数
        String payload = CodeSceneEnum.sceneIsMobile(reqDTO.getScene()) ? reqDTO.getMobile() : reqDTO.getEmail();
        String incrKey = RedisKeyConstant.getVerifyCountKey(payload);
        RedisUtil.incr(incrKey, 1);
        String codeControlKey = RedisKeyConstant.getCodeControlKey(payload);
        // 查询redis
        String codeExpire = RedisUtil.hget(codeControlKey, reqDTO.getCode());
        // 验证码是否已过期
        if (!StringUtils.hasLength(codeExpire)) {
            throw new ServiceException(ServiceErrorCodes.AUTH_CODE_ERROR);
        }
        String[] split = codeExpire.split(SysConstant.SEPARATOR);
        Long expire = Long.valueOf(split[1]);
        if (System.currentTimeMillis() > expire) {
            throw new ServiceException(ServiceErrorCodes.AUTH_CODE_EXPIRED);
        }

        // 验证码是否已被使用
        if (Integer.valueOf(split[0]) == 1) {
            throw new ServiceException(ServiceErrorCodes.AUTH_CODE_ERROR);
        }

        // 更新验证码使用状态
        split[0] = "1";
        String newExpire = StringUtils.arrayToDelimitedString(split, SysConstant.SEPARATOR);
        RedisUtil.hset(codeControlKey, reqDTO.getCode(), newExpire);
        // TODO 记录日志
    }

    @Override
    public void sendBandingCode(BindingCodeReqVo data, Long userId) {
        // 获取账号信息
        AccountDo accountDo = accountMapper.selectById(userId);
        if (accountDo == null) {
            throw new ServiceException(ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR);
        }
        SimpleCodeReqDTO codeReqDTO = new SimpleCodeReqDTO()
                .setUserId(userId)
                .setSign(data.getSign())
                .setScene(data.getScene().getScene());
        if (CodeSceneEnum.sceneIsMobile(data.getScene())) {
            codeReqDTO.setMobile(CommonUtils.decryptStr(accountDo.getMobile()));
        } else {
            codeReqDTO.setEmail(CommonUtils.decryptStr(accountDo.getEmail()));
        }

        // 判断使用场景是否合法
        checkCodeSceneValidate(codeReqDTO);
        // 判断能否创建验证码
        String code = canCreateCode(codeReqDTO);
        codeReqDTO.setCode(code);
        // 发送短信验证码
        createSendLog(codeReqDTO);
    }

    @Override
    public void handlerSendVerifyCode(List<CodeSendDTO> collect) {
        // 通过mq异步发送验证码
        ArrayList<CodeLogDo> list = new ArrayList<>();
        for (CodeSendDTO codeLogDto : collect) {
            SimpleSendResultDTO sendResult;
            if (StringUtils.hasText(codeLogDto.getEmail())) {
                sendResult = requestSendEmail(codeLogDto);
            } else {
                sendResult = requestSendMobile(codeLogDto);
            }
            CodeLogDo logDo = new CodeLogDo();
            logDo.setUid(codeLogDto.getUid());
            if (sendResult.isResult()) {
                logDo.setSendStatus(3);
            } else {
                logDo.setSendStatus(2);
                log.error("[发送短信验证码] --- 短信发送失败， errorInfo: {}", sendResult.getMark());
            }
            logDo.setSendTime(new Date()).setUpdateTime(logDo.getSendTime());
            list.add(logDo);
        }
        codeLogMapper.updateById(list);
    }

    /**
     * @description: 创建验证码发送日志
     * @param: SimpleCodeReqDTO
     * @return: Long
     * @author: lovbe0210
     * @date: 2024/9/14 13:34
     */
    public void createSendLog(SimpleCodeReqDTO codeReqDTO) {
        // 校验短信模板是否合法
        VCodeTemplateDO template = validateSmsTemplate(CodeSceneEnum.getCodeByScene(codeReqDTO.getScene()));

        // 构建有序的模板参数
        Map<String, Object> templateParams = buildTemplateParams(template, codeReqDTO);

        // 发送 MQ 消息，异步执行发送短信
        SimpleSendResultDTO sendResult = sendVerifyCodeMessage(codeReqDTO, template, templateParams);
        String logDesc = "";
        if (sendResult.isResult()) {
            logDesc = "验证码发送成功";
        } else {
            logDesc = "验证码发送失败， errorInfo: " + sendResult.getMark() + "，sendLogId: " + sendResult.getSendLogId();
        }
        recordVerifyCodeLog(codeReqDTO.getUserId(), codeReqDTO.getEmail(), logDesc);
    }

    private SimpleSendResultDTO requestSendMobile(CodeSendDTO codeSendDTO) {
        Map<String, Object> querys = new HashMap<>();
        querys.put("mobile", codeSendDTO.getMobile());
        Map<String, Object> param = codeSendDTO.getTemplateParam();
        if (!CollectionUtils.isEmpty(param)) {
            String joinParam = param.entrySet().stream()
                    .map(entry -> "**" + entry.getKey() + "**:" + entry.getValue())
                    .collect(Collectors.joining(","));
            querys.put("param", joinParam);
        }
        querys.put("smsSignId", properties.getSmsSignId());
        querys.put("templateId", codeSendDTO.getTemplateId());
        String queryStr = querys.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        // 构建请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(properties.getSmsUrl() + "?" + queryStr))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "APPCODE " + properties.getSmsAppcode())
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            // 发起请求
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            JSONObject bodyParse = JsonUtils.parseObject(body, JSONObject.class);
            if (bodyParse == null) {
                return new SimpleSendResultDTO(false, codeSendDTO.getUid(), "发送请求失败，return null， status: " + response.statusCode());
            }
            Integer code = bodyParse.getInteger("code");
            if (code == 0) {
                return new SimpleSendResultDTO(true, codeSendDTO.getUid(), null);
            } else {
                return new SimpleSendResultDTO(false, codeSendDTO.getUid(), "msg：" + bodyParse.getString("msg") + ", code: " + code);
            }
        } catch (Exception e) {
            log.error("[发送短信验证码] --- 短信发送失败， errorInfo: {}", e.toString());
            return new SimpleSendResultDTO(false, codeSendDTO.getUid(), e.toString());
        }
    }

    private SimpleSendResultDTO requestSendEmail(CodeSendDTO codeSendDTO) {
        String from = "IT充电站 <" + properties.getServerEmail() + ">";
        MailAccount mailAccount = new MailAccount().setFrom(from).setAuth(true)
                .setUser(properties.getEmailUsername()).setPass(properties.getEmailPassword())
                .setAuth(true)
                .setHost(properties.getEmailHost()).setPort(properties.getEmailPort())
                .setSslEnable(properties.getSslEnable()).setStarttlsEnable(properties.getStarttlsEnable());
        try {
            String content = formatTemplateContent(codeSendDTO.getTemplateContent(), codeSendDTO.getTemplateParam());
            MailUtil.send(mailAccount, codeSendDTO.getEmail(), codeSendDTO.getTitle(), content, true);
            return new SimpleSendResultDTO(true);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[发送邮箱验证码] --- sendId: {}, errorInfo: {}", codeSendDTO.getUid(), e.toString());
            return new SimpleSendResultDTO(false, codeSendDTO.getUid(), e.toString());
        }
    }

    private SimpleSendResultDTO sendVerifyCodeMessage(SimpleCodeReqDTO codeReqDTO, VCodeTemplateDO template, Map<String, Object> templateParams) {
        CodeSceneEnum codeByScene = CodeSceneEnum.getCodeByScene(codeReqDTO.getScene());
        String content = formatTemplateContent(template.getContent(), templateParams);
        // 创建发送日志
        CodeLogDo codeLogDO = new CodeLogDo()
                .setUserId(codeReqDTO.getUserId())
                .setSceneCode(codeByScene.getTemplateCode())
                .setEmail(CodeSceneEnum.sceneIsEmail(codeReqDTO.getScene()) ? codeReqDTO.getEmail() : null)
                .setMobile(CodeSceneEnum.sceneIsMobile(codeReqDTO.getScene()) ? codeReqDTO.getMobile() : null)
                .setTitle(template.getName())
                .setContent(content)
                .setSendStatus(0);
        codeLogDO.setUid(YitIdHelper.nextId())
                .setStatus(CommonStatusEnum.NORMAL.getStatus())
                .setCreateTime(new Date())
                .setUpdateTime(codeLogDO.getCreateTime());
        codeLogMapper.insert(codeLogDO);
        // 通过消息异步发送
        CodeSendDTO sendDTO = new CodeSendDTO()
                .setUid(codeLogDO.getUid())
                .setScene(codeReqDTO.getScene())
                .setEmail(codeReqDTO.getEmail())
                .setMobile(codeReqDTO.getMobile())
                .setTitle(template.getName())
                .setTemplateContent(template.getContent())
                .setTemplateParam(templateParams)
                .setTemplateId(template.getApiTemplateId());
        commonService.sendMessage(appName, sendCodeTopic, sendDTO);
        return new SimpleSendResultDTO(true);
    }

    private String formatTemplateContent(String content, Map<String, Object> templateParams) {
        content = content.replace("**code**", "{code}")
                .replace("**minute**", "{minute}");
        return StrUtil.format(content, templateParams);
    }


    /**
     * @description: 判断能否发送验证码
     * redis中的hashValue由3段组成，分别代表当前code有没有被使用、过期时间、频率控制倍数
     * @param: AuthCodeReqDTO
     * @return: String
     * @author: lovbe0210
     * @date: 2024/8/16 16:50
     */
    private String canCreateCode(SimpleCodeReqDTO codeReqDTO) {
        boolean isMobile = CodeSceneEnum.sceneIsMobile(codeReqDTO.getScene());
        String payload = isMobile ? codeReqDTO.getMobile() : codeReqDTO.getEmail();
        String codeControlKey = RedisKeyConstant.getCodeControlKey(payload);
        RLock lock = redissonClient.getLock(RedisKeyConstant.getCodeLockKey(payload));
        try {
            if (lock.tryLock(5, -1, TimeUnit.SECONDS)) {
                // 先验证uniqueId和ip地址不受限制
                String bitwised = bitwiseInvert(codeReqDTO.getSign());
                String decodedStr = Base64.decodeStr(bitwised);
                JSONObject parseObj = JsonUtils.parseObject(decodedStr, JSONObject.class);
                String uniqueId = parseObj.getString(SysConstant.UNIQUE_ID);
                CommonUtils.checkVerifyCodeFrequencyLimit(uniqueId, properties.getVerifyCodeLimit());
                Map<Object, Object> codeExpireMap = RedisUtil.hgetMap(codeControlKey);
                String code = RandomUtil.randomNumbers(6);
                if (CollectionUtils.isEmpty(codeExpireMap) || codeExpireMap.size() <= 2) {
                    // 先将历史验证码置为已使用
                    if (!CollectionUtils.isEmpty(codeExpireMap)) {
                        codeExpireMap.forEach((key, value) -> {
                            if (value != null && ((String) value).startsWith("0_")) {
                                String newValue = "1_" + ((String) value).substring(2);
                                RedisUtil.hset(codeControlKey, (String) key, newValue);
                            }
                        });
                    }
                    // hash中的value用于存放key的失效时间和延长倍数
                    String expireValue = "0_" + (System.currentTimeMillis() + RedisKeyConstant.EXPIRE_10_MIN * 1000) + "_0";
                    RedisUtil.hset(codeControlKey, code, expireValue, RedisKeyConstant.EXPIRE_1_HOUR);
                    return code;
                }

                // 三次之后的频率限制以30分钟为基数，然后取幂次结果
                Object recentExpireValue = codeExpireMap.values().stream().sorted((ev1, ev2) -> {
                    String et1 = ((String) ev1).split(SysConstant.SEPARATOR)[1];
                    String et2 = ((String) ev2).split(SysConstant.SEPARATOR)[1];
                    return Math.toIntExact(Long.valueOf(et2) - Long.valueOf(et1));
                }).collect(Collectors.toList()).get(0);
                String[] split = ((String) recentExpireValue).split(SysConstant.SEPARATOR);
                int multiple = Integer.valueOf(split[2]);
                // 这里需要减去之前加的过期时间10分钟
                long canSendTime = Long.valueOf(split[1]) + (multiple + 1) * RedisKeyConstant.EXPIRE_30_MIN * 1000 - RedisKeyConstant.EXPIRE_10_MIN * 1000;
                if (System.currentTimeMillis() <= canSendTime) {
                    recordVerifyCodeLog(codeReqDTO.getUserId(), payload, GlobalErrorCodes.TOO_MANY_REQUESTS.getMsg());
                    throw new ServiceException(GlobalErrorCodes.TOO_MANY_REQUESTS);
                }
                String expireValue = "0_" + (System.currentTimeMillis() + RedisKeyConstant.EXPIRE_10_MIN * 1000) + SysConstant.SEPARATOR + (1 << multiple++);
                RedisUtil.hset(codeControlKey, code, expireValue, RedisKeyConstant.EXPIRE_1_HOUR * (multiple));
                recordVerifyCodeLog(codeReqDTO.getUserId(), payload, "success");
                return code;
            }
            throw new ServiceException("获取锁超时，payload: " + payload);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("[创建验证码] --- 获取锁或验证码数据解析异常，errorInfo: {}", e.toString());
            throw new ServiceException(ServiceErrorCodes.SIMPLE_CODE_SEND_FAILED);
        } finally {
            try {
                lock.unlock();
            } catch (Exception e) {
                log.error("[创建验证码] --- 锁释放失败，errorInfo: {}", e.toString());
            }
        }
    }

    /**
     * @return VCodeTemplateDO
     * @description 获取验证码模板
     * @param[1] templateCode
     * @author lovbe0210
     * @date 2024/8/18 21:06
     */
    VCodeTemplateDO validateSmsTemplate(CodeSceneEnum sceneEnum) {
        // 获得验证码模板
        List<VCodeTemplateDO> template = simpleCodeMapper.selectList(
                new LambdaQueryWrapper<VCodeTemplateDO>()
                        .eq(VCodeTemplateDO::getCode, sceneEnum.getTemplateCode())
                        .eq(VCodeTemplateDO::getStatus, CommonStatusEnum.NORMAL.getStatus())
                        .orderByDesc(VCodeTemplateDO::getUpdateTime));
        // 验证码模板不存在
        if (CollectionUtils.isEmpty(template)) {
            log.error("[发送短信] --- 系统错误，未获取到场景：{}的模板内容", sceneEnum.getTemplateCode());
            throw new ServiceException(ServiceErrorCodes.SIMPLE_CODE_SEND_FAILED);
        }
        VCodeTemplateDO templateDO = template.get(0);
        // 必须要有参数，填写code
        List<String> params = templateDO.getParams();
        if (CollectionUtils.isEmpty(params)) {
            log.error("[发送短信] --- 系统错误，验证码模板参数为空，模板不可用，模板id：{}", templateDO.getUid());
            throw new ServiceException(ServiceErrorCodes.SIMPLE_CODE_SEND_FAILED);
        }
        return templateDO;
    }

    /**
     * 将参数模板，处理成有序的 KeyValue 数组
     * <p>
     * 原因是，部分短信平台并不是使用 key 作为参数，而是数组下标，例如说 <a href="https://cloud.tencent.com/document/product/382/39023">腾讯云</a>
     *
     * @param template   短信模板
     * @param codeReqDTO 原始参数
     * @return 处理后的参数
     */
    Map<String, Object> buildTemplateParams(VCodeTemplateDO template, SimpleCodeReqDTO codeReqDTO) {
        List<String> params = template.getParams();
        Map<String, Object> paramMap = new LinkedHashMap<>();
        params.forEach(param -> {
            if (Objects.equals(param, "code")) {
                paramMap.put(param, codeReqDTO.getCode());
            } else if (Objects.equals(param, "mobile")) {
                paramMap.put(param, codeReqDTO.getMobile());
            } else if (Objects.equals(param, "email")) {
                paramMap.put(param, codeReqDTO.getEmail());
            } else if (Objects.equals(param, "minute")) {
                paramMap.put(param, properties.getMinutes());
            }

        });
        return paramMap;
    }

    /**
     * @description: 判断验证码使用场景是否合法、
     * @param: SmsCodeReqVo
     * @return: void
     * @author: lovbe0210
     * @date: 2024/9/14 11:14
     */
    private void checkCodeSceneValidate(SimpleCodeReqDTO codeReqDTO) {
        LambdaQueryWrapper<AccountDo> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.equals(codeReqDTO.getScene(), CodeSceneEnum.MOBILE_RESET_PASSWORD.getScene())) {
            queryWrapper.eq(AccountDo::getMobile, codeReqDTO.getMobile());
        } else if (Objects.equals(codeReqDTO.getScene(), CodeSceneEnum.EMAIL_RESET_PASSWORD.getScene())) {
            queryWrapper.eq(AccountDo::getEmail, codeReqDTO.getEmail());
        } else {
            queryWrapper.eq(AccountDo::getUid, codeReqDTO.getUserId());
        }
        AccountDo accountDo = accountMapper.selectOne(queryWrapper);
        switch (CodeSceneEnum.getCodeByScene(codeReqDTO.getScene())) {
            case BIND_MOBILE:
                if (accountDo == null ||
                        !StringUtils.hasLength(accountDo.getEmail()) ||
                        StringUtils.hasLength(accountDo.getMobile())) {
                    throw new ServiceException(ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR);
                }
                break;
            case VERIFY_MOBILE:
                if (accountDo == null) {
                    throw new ServiceException(ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR);
                }
                // 通过手机号获取相同手机账号
                Long mobileCount = accountMapper.selectCount(new LambdaQueryWrapper<AccountDo>()
                        .eq(AccountDo::getMobile, CommonUtils.encryptStr(codeReqDTO.getMobile()))
                        .eq(AccountDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
                if (mobileCount > 0) {
                    throw new ServiceException(ServiceErrorCodes.USER_MOBILE_USED);
                }
                break;
            case BIND_EMAIL:
                if (accountDo == null ||
                        !StringUtils.isEmpty(accountDo.getMobile()) ||
                        StringUtils.hasLength(accountDo.getEmail())) {
                    throw new ServiceException(ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR);
                }
                break;
            case VERIFY_EMAIL:
                if (accountDo == null) {
                    throw new ServiceException(ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR);
                }
                // 通过邮箱号获取相同邮箱账号
                Long emailCount = accountMapper.selectCount(new LambdaQueryWrapper<AccountDo>()
                        .eq(AccountDo::getEmail, CommonUtils.encryptStr(codeReqDTO.getEmail()))
                        .eq(AccountDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
                if (emailCount > 0) {
                    throw new ServiceException(ServiceErrorCodes.USER_EMAIL_USED);
                }
                break;
            case MOBILE_UPDATE_EMAIL:
            case MOBILE_UPDATE_MOBILE:
            case MOBILE_UPDATE_PASSWORD:
                if (accountDo == null || StringUtils.isEmpty(accountDo.getMobile())) {
                    throw new ServiceException(ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR);
                }
                break;
            case EMAIL_UPDATE_MOBILE:
            case EMAIL_UPDATE_EMAIL:
            case EMAIL_UPDATE_PASSWORD:
                if (accountDo == null || StringUtils.isEmpty(accountDo.getEmail())) {
                    throw new ServiceException(ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR);
                }
                break;
            case MOBILE_RESET_PASSWORD:
                if (accountDo == null || !StringUtils.hasLength(accountDo.getMobile())) {
                    throw new ServiceException(ServiceErrorCodes.USER_ACCOUNT_NOT_EXISTS);
                }
                break;
            case EMAIL_RESET_PASSWORD:
                if (accountDo == null || !StringUtils.hasLength(accountDo.getEmail())) {
                    throw new ServiceException(ServiceErrorCodes.USER_ACCOUNT_NOT_EXISTS);
                }
                break;
            case MOBILE_LOGIN:
            case EMAIL_LOGIN:
                return;
            default:
                if (accountDo == null) {
                    throw new ServiceException(ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR);
                }
        }
        if (CommonStatusEnum.isDisable(accountDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.ACCOUNT_DISABLED);
        }
        if (CommonStatusEnum.isDelete(accountDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.AUTH_ACCOUNT_STATUS_ERROR);
        }
    }

    /**
     * @description 记录验证码日志
     * @param[1] uid
     * @param[2] mobile
     * @param[3] logTypeEnum
     * @param[4] loginResultEnum
     * @author lovbe0210
     * @date 2024/8/14 22:47
     */
    private void recordVerifyCodeLog(Long userId, String payload, String actionDesc) {
        // TODO 记录日志
        String userIp = ServletUtils.getClientIP();
        log.info("[simpleCode] - userId: {}, payload：{}, status: {}, userIp: {}", userId, payload, actionDesc, userIp);
    }
}
