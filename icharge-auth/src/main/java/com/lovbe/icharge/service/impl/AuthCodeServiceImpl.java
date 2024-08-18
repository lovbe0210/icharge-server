package com.lovbe.icharge.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ErrorCode;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.util.servlet.ServletUtils;
import com.lovbe.icharge.entity.dto.VCodeTemplateDO;
import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.service.AuthCodeService;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Valid
public class AuthCodeServiceImpl implements AuthCodeService {
    @Override
    public String sendMobileCode(AuthCodeReqDTO reqDTO) {
        Assert.notNull(reqDTO.getScene(), GlobalErrorCodes.NOT_IMPLEMENTED.getMsg(), GlobalErrorCodes.NOT_IMPLEMENTED.getCode());
        // 判断并创建验证码
        String code = canCreateCode(reqDTO);
        // 发送短信验证码
        HashMap<String, String> paramMap = MapUtil.of(SysConstant.CODE, code);
        paramMap.put(SysConstant.MOBILE, reqDTO.getMobile());
        sendSingleSms(paramMap, null, reqDTO.getScene().getTemplateCode());
        return code;
    }

    @Override
    public void sendEmailCode(AuthCodeReqDTO reqDTO) {

    }

    @Override
    public void useVerifyCode(@Valid AuthCodeReqDTO reqDTO) throws ServiceException {
        String key = RedisKeyConstant.getVerifyCode(reqDTO.getScene(), reqDTO.getMobile(), reqDTO.getEmail());
        // 查询redis
        String code = (String) RedisUtil.get(key);
        if (!StringUtils.hasLength(code)) {
            String incrKey = RedisKeyConstant.getVerifyCount(reqDTO.getScene(), reqDTO.getMobile(), reqDTO.getEmail());
            RedisUtil.incr(incrKey, 1);
            throw new ServiceException(ServiceErrorCodes.AUTH_CODE_EXPIRED);
        }

        if (!Objects.equals(reqDTO.getCode(), code)) {
            String incrKey = RedisKeyConstant.getVerifyCount(reqDTO.getScene(), reqDTO.getMobile(), reqDTO.getEmail());
            RedisUtil.incr(incrKey, 1);
            saveErrorLog(reqDTO, ServiceErrorCodes.AUTH_CODE_ERROR);
            throw new ServiceException(ServiceErrorCodes.AUTH_CODE_ERROR);
        }
        // TODO 记录日志
    }

    private void saveErrorLog(AuthCodeReqDTO reqDTO, ErrorCode authCodeError) {
        // TODO 记录日志
        log.error("[验证码错误]--loginInfo: {}", JSONUtil.toJsonStr(reqDTO));
    }


    /**
     * @description: 判断能否发送验证码
     * @param: AuthCodeReqDTO
     * @return: String
     * @author: lovbe0210
     * @date: 2024/8/16 16:50
     */
    private String canCreateCode(AuthCodeReqDTO codeReqDTO) {
        boolean isMobile = CodeSceneEnum.sceneIsMobile(codeReqDTO.getScene());
        String payload = isMobile ? codeReqDTO.getMobile() : codeReqDTO.getEmail();
        String codeExpireKey = RedisKeyConstant.getCodeFrequencyKey(payload);
        Map<Object, Object> codeExpireMap = RedisUtil.hgetMap(codeExpireKey);
        String code = RandomUtil.randomNumbers(6);

        // 如果1小时内的发送次数小于3，则不做限制
        if (CollectionUtils.isEmpty(codeExpireMap) || codeExpireMap.size() <= 2) {
            // hash中的value用于存放key的失效时间和延长倍数
            String expireValue = System.currentTimeMillis() + RedisKeyConstant.EXPIRE_10_MIN + "_0";
            RedisUtil.hset(codeExpireKey, code, expireValue, RedisKeyConstant.EXPIRE_1_HOUR);
            return code;
        }

        // 三次之后的频率限制以30分钟为基数
        Object recentExpireValue = codeExpireMap.values().stream()
                .sorted((ev1, ev2) -> {
                    String et1 = ((String) ev1).split("_")[0];
                    String et2 = ((String) ev1).split("_")[0];
                    return Math.toIntExact(Long.valueOf(et2) - Long.valueOf(et1));
                })
                .findFirst()
                .get();
        String[] split = ((String) recentExpireValue).split("_");
        int multiple = Integer.valueOf(split[1]);
        long canSendTime = Long.valueOf(split[0]) + (multiple + 1) * RedisKeyConstant.EXPIRE_30_MIN;
        if (System.currentTimeMillis() <= canSendTime) {
            recordVerifyCodeLog(null, payload, GlobalErrorCodes.TOO_MANY_REQUESTS.getMsg());
            throw new ServiceException(GlobalErrorCodes.TOO_MANY_REQUESTS);
        }
        String expireValue = System.currentTimeMillis() + RedisKeyConstant.EXPIRE_10_MIN + "_" + (++multiple);
        RedisUtil.hset(codeExpireKey, code, expireValue, RedisKeyConstant.EXPIRE_1_HOUR * (++multiple));
        recordVerifyCodeLog(null, payload, "success");
        return code;
    }

    public Long sendSingleSms(Map<String, String> templateParams, Long userId, String templateCode) {
        // 校验短信模板是否合法
        VCodeTemplateDO template = validateSmsTemplate(templateCode);

        // 校验手机号码是否存在
        if (!StringUtils.hasLength(templateParams.get(SysConstant.MOBILE))) {
            throw new ServiceException(ServiceErrorCodes.MOBILE_NOT_EXIST);
        }
        // 构建有序的模板参数。为什么放在这个位置，是提前保证模板参数的正确性，而不是到了插入发送日志
        List<Map<String, String>> newTemplateParams = buildTemplateParams(template, templateParams);

        // 创建发送日志。如果模板被禁用，则不发送短信，只记录日志
        Boolean isSend = CommonStatusEnum.ENABLE.getStatus().equals(template.getStatus())
                && CommonStatusEnum.ENABLE.getStatus().equals(smsChannel.getStatus());
        String content = smsTemplateService.formatSmsTemplateContent(template.getContent(), templateParams);
        Long sendLogId = smsLogService.createSmsLog(mobile, userId, userType, isSend, template, content, templateParams);

        // 发送 MQ 消息，异步执行发送短信
        if (isSend) {
            smsProducer.sendSmsSendMessage(sendLogId, mobile, template.getChannelId(),
                    template.getApiTemplateId(), newTemplateParams);
        }
        return sendLogId;
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
    private void recordVerifyCodeLog(Long uid, String payload, String actionDesc) {
        // 插入登陆日志 TODO
        String userIp = ServletUtils.getClientIP();
        log.info("[Login] - userId: {}, payload：{}, status: {}, userIp: {}", uid, payload, actionDesc, userIp);
    }

    /**
     * @description 获取验证码模板
     * @param[1] templateCode
     * @return VCodeTemplateDO
     * @author lovbe0210
     * @date 2024/8/18 21:06
     */
    VCodeTemplateDO validateSmsTemplate(String templateCode) {
        // 获得短信模板。考虑到效率，从缓存中获取
        VCodeTemplateDO template = vCodeTemplateService.getSmsTemplateByCodeFromCache(templateCode);
        // 短信模板不存在
        if (template == null) {
            throw new ServiceException(ServiceErrorCodes.VCODE_TEMPLATE_NOT_EXISTS);
        }
        return template;
    }

    /**
     * 将参数模板，处理成有序的 KeyValue 数组
     * <p>
     * 原因是，部分短信平台并不是使用 key 作为参数，而是数组下标，例如说 <a href="https://cloud.tencent.com/document/product/382/39023">腾讯云</a>
     *
     * @param template       短信模板
     * @param templateParams 原始参数
     * @return 处理后的参数
     */
    List<Map<String, Object>> buildTemplateParams(VCodeTemplateDO template, Map<String, Object> templateParams) {
        return template.getParams().stream().map(key -> {
            Object value = templateParams.get(key);
            if (value == null) {
//                throw ServiceException("SMS_SEND_MOBILE_TEMPLATE_PARAM_MISS", key);
            }
            return MapUtil.of(key, value);
        }).collect(Collectors.toList());
    }
}
