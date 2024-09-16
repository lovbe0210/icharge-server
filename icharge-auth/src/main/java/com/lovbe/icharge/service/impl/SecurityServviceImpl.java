package com.lovbe.icharge.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.common.util.validation.ValidationUtils;
import com.lovbe.icharge.dto.vo.SecServiceSvVo;
import com.lovbe.icharge.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/9/15 8:58
 * @Description: MS
 */
@Service
@Slf4j
public class SecurityServviceImpl implements SecurityService {
    @Override
    public ResponseBean setSliderValidCookie(SecServiceSvVo scvo) {
        // 解析data
        String sign = scvo.getSign();
        try {
            String decodedStr = Base64.decodeStr(ValidationUtils.bitwiseInvert(sign));
            JSONObject parseObj = JSONUtil.parseObj(decodedStr);
            String uniqueId = parseObj.getStr(SysConstant.UNIQUE_ID);
            Long timestamp = parseObj.getLong(SysConstant.TIMESTAMP);
            if (!StringUtils.hasLength(uniqueId) || timestamp == null) {
                throw new ServiceException("页面唯一标识uniqueId或timestamp为空");
            }
            String redisKey = RedisKeyConstant.getSliderVerifyCookie(uniqueId, timestamp);
            // 页面唯一标识保存
            Object svToken = RedisUtil.get(redisKey);
            if (svToken == null) {
                svToken = IdUtil.simpleUUID();
            }
            RedisUtil.set(redisKey, svToken, RedisKeyConstant.EXPIRE_2_HOUR);
            // 保存滑块验证cookie
            String svTokenKey = RedisKeyConstant.geSvToken((String) svToken);
            RedisUtil.set(svTokenKey, svToken, RedisKeyConstant.EXPIRE_1_HOUR);
            Map<String, Object> map = MapUtil.of(SysConstant.ID, uniqueId);
            map.put(SysConstant.TN, svToken);
            return ResponseBean.ok(map);
        }catch (Exception e) {
            log.error("[身份验证] --- 获取页面埋点cookie参数解析错误, errorInfo: {}", e.toString());
            throw new ServiceException(GlobalErrorCodes.BAD_REQUEST);
        }
    }
}
