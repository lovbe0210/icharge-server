package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.PreferenceSettingVo;
import com.lovbe.icharge.common.util.fallback.BaseFallback;
import org.springframework.stereotype.Component;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/11 22:32
 * @Description: MS
 */
@Component
public class IndividuationServiceFallback extends BaseFallback implements IndividuationService{
    @Override
    public ResponseBean<PreferenceSettingVo> getPreferenceSetting(Long userId) {
        return getFallBack();
    }
}
