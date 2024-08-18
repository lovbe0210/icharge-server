package com.lovbe.icharge.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lovbe.icharge.common.exception.ErrorCode;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.util.servlet.ServletUtils;
import com.lovbe.icharge.entity.dto.AuthCodeReqDTO;
import com.lovbe.icharge.enums.CodeSceneEnum;
import com.lovbe.icharge.enums.LoginResultEnum;
import com.lovbe.icharge.service.AuthCodeService;
import com.lovbe.icharge.util.RedisKeyConstant;
import com.lovbe.icharge.util.RedisUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import static cn.hutool.core.util.RandomUtil.randomInt;

@Slf4j
@Service
@Valid
public class AuthCodeServiceImpl implements AuthCodeService {
    @Override
    public String sendMobileCode(AuthCodeReqDTO reqDTO) {
        Assert.notNull(reqDTO.getScene(), GlobalErrorCodes.NOT_IMPLEMENTED.getMsg(), GlobalErrorCodes.NOT_IMPLEMENTED.getCode());
        // 判断并创建验证码
        String code = canCreateCode(reqDTO);
        // 发送验证码
        smsSendService.sendSingleSms(reqDTO.getMobile(), null, null,
                sceneEnum.getTemplateCode(), MapUtil.of("code", code));
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
}
