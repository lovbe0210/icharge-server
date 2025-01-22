package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.model.vo.RelationshipVo;
import com.lovbe.icharge.common.util.fallback.BaseFallback;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SocialServiceFallback extends BaseFallback implements SocialService {

    @Override
    public ResponseBean<List<RelationshipVo>> getRelationshipList(Long userId, String targetShip) {
        return getFallBack();
    }
}
