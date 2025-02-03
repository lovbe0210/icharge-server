package com.lovbe.icharge.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ErrorCode;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.dto.AccountDo;
import com.lovbe.icharge.common.model.dto.SimpleCodeReqDTO;
import com.lovbe.icharge.common.model.dto.VCodeTemplateDO;
import com.lovbe.icharge.common.model.vo.BindingCodeReqVo;
import com.lovbe.icharge.common.model.vo.EmailCodeReqVo;
import com.lovbe.icharge.common.model.vo.SmsCodeReqVo;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.common.util.servlet.ServletUtils;
import com.lovbe.icharge.entity.dto.SimpleSendResultDTO;
import com.lovbe.icharge.dao.AccountMapper;
import com.lovbe.icharge.dao.SimpleCodeMapper;
import com.lovbe.icharge.service.SimpleCodeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@Valid
public class SimpleCodeServiceImpl implements SimpleCodeService {
    @Resource
    private SimpleCodeMapper simpleCodeMapper;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void sendSmsCode(SmsCodeReqVo reqVo, Long userId) {
        SimpleCodeReqDTO codeReqDTO = new SimpleCodeReqDTO()
                .setMobile(reqVo.getMobile())
                .setScene(reqVo.getScene())
                .setUserId(userId)
                .setUsedIp(ServletUtils.getClientIP());
        // 判断使用场景是否合法
        checkCodeSceneValidate(codeReqDTO);
        // 判断能否创建验证码
        String code = canCreateCode(codeReqDTO);
        codeReqDTO.setCode(code);
        // 发送短信验证码
        sendSimpleCode(codeReqDTO);
    }

    @Override
    public void sendEmailCode(EmailCodeReqVo reqVo, Long userId) {
        // 判断并创建验证码
        SimpleCodeReqDTO codeReqDTO = new SimpleCodeReqDTO()
                .setEmail(reqVo.getEmail())
                .setScene(reqVo.getScene().getScene())
                .setUserId(userId)
                .setUsedIp(ServletUtils.getClientIP());
        // 判断使用场景是否合法
        checkCodeSceneValidate(codeReqDTO);
        // 判断能否创建验证码
        String code = canCreateCode(codeReqDTO);
        codeReqDTO.setCode(code);
        // 发送短信验证码
        sendSimpleCode(codeReqDTO);
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
        String[] split = codeExpire.split("_");
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
        String newExpire = StringUtils.arrayToDelimitedString(split, "_");
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
        sendSimpleCode(codeReqDTO);

    }

    /**
     * @description: 发送验证码
     * @param: SimpleCodeReqDTO
     * @return: Long
     * @author: lovbe0210
     * @date: 2024/9/14 13:34
     */
    public void sendSimpleCode(SimpleCodeReqDTO codeReqDTO) {
        // 校验短信模板是否合法
        VCodeTemplateDO template = validateSmsTemplate(CodeSceneEnum.getCodeByScene(codeReqDTO.getScene()));

        // 构建有序的模板参数。为什么放在这个位置，是提前保证模板参数的正确性，而不是到了插入发送日志
        List<Map<String, Object>> newTemplateParams = buildTemplateParams(template, codeReqDTO);

        // TODO 发送 MQ 消息，异步执行发送短信
        SimpleSendResultDTO sendResult = new SimpleSendResultDTO();
        /*SimpleSendResultDTO result = sendSmsSendMessage(mobile, template.getChannelId(),
                template.getApiTemplateId(), newTemplateParams);*/
        String logDesc = "";
        if (sendResult.isResult()) {
            logDesc = "验证码发送成功";
        } else {
            logDesc = "验证码发送失败， errorInfo: " + sendResult.getMark() + "，sendLogId: " + sendResult.getSendLogId();
        }
        recordVerifyCodeLog(codeReqDTO.getUserId(), codeReqDTO.getEmail(), logDesc);

        // TODO 记录完日志，如果发送失败抛出异常
        if (sendResult.isResult()) {
            log.error("[发送验证码] --- {}", logDesc);
            throw new ServiceException(ServiceErrorCodes.SIMPLE_CODE_SEND_FAILED);
        } else {
            log.error("[--------------- code: {} -----------------]", codeReqDTO.getCode());
        }
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
                Map<Object, Object> codeExpireMap = RedisUtil.hgetMap(codeControlKey);
                String code = RandomUtil.randomNumbers(6);

                // TODO 如果1小时内的发送次数小于3，则不做限制
                if (CollectionUtils.isEmpty(codeExpireMap) || codeExpireMap.size() <= 2000000000) {
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
                    String et1 = ((String) ev1).split("_")[1];
                    String et2 = ((String) ev2).split("_")[1];
                    return Math.toIntExact(Long.valueOf(et2) - Long.valueOf(et1));
                }).collect(Collectors.toList()).get(0);
                String[] split = ((String) recentExpireValue).split("_");
                int multiple = Integer.valueOf(split[2]);
                // 这里需要减去之前加的过期时间10分钟
                long canSendTime = Long.valueOf(split[0]) + (multiple + 1) * RedisKeyConstant.EXPIRE_30_MIN * 1000 - RedisKeyConstant.EXPIRE_10_MIN * 1000;
                if (System.currentTimeMillis() <= canSendTime) {
                    recordVerifyCodeLog(codeReqDTO.getUserId(), payload, GlobalErrorCodes.TOO_MANY_REQUESTS.getMsg());
                    throw new ServiceException(GlobalErrorCodes.TOO_MANY_REQUESTS);
                }
                String expireValue = "0_" + (System.currentTimeMillis() + RedisKeyConstant.EXPIRE_10_MIN * 1000) + "_" + (1 << multiple++);
                RedisUtil.hset(codeControlKey, code, expireValue, RedisKeyConstant.EXPIRE_1_HOUR * (multiple));
                recordVerifyCodeLog(codeReqDTO.getUserId(), payload, "success");
                return code;
            }
            throw new ServiceException("获取锁超时，payload: " + payload);
        } catch (InterruptedException e) {
            log.error("[创建验证码] --- 获取锁或验证码数据解析异常，errorInfo: {}", e.toString());
            throw new ServiceException(ServiceErrorCodes.SIMPLE_CODE_SEND_FAILED);
        } catch (ServiceException e) {
            throw e;
        }finally {
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
    List<Map<String, Object>> buildTemplateParams(VCodeTemplateDO template, SimpleCodeReqDTO codeReqDTO) {
        List<String> params = template.getParams();
        List<Map<String, Object>> collect = params.stream().map(key -> {
            Map<String, Object> map = new HashMap<>();
            String code = codeReqDTO.getCode();
            if (!StringUtils.hasLength(code)) {
                log.error("[发送验证码] --- 构建验证码模板参数错误，未获取到验证码值");
                throw new ServiceException(ServiceErrorCodes.SIMPLE_CODE_SEND_FAILED);
            }
            map.put(key, code);
            return map;
        }).collect(Collectors.toList());
        return collect;
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
        // 插入登陆日志 TODO
        String userIp = ServletUtils.getClientIP();
        log.info("[Login] - userId: {}, payload：{}, status: {}, userIp: {}", userId, payload, actionDesc, userIp);
    }

    private void saveErrorLog(SimpleCodeReqDTO reqDTO, ErrorCode authCodeError) {
        // TODO 记录日志
        log.error("[验证码错误]--loginInfo: {}", JSONUtil.toJsonStr(reqDTO));
    }
}
