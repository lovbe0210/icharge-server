package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.RelationshipDo;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.model.vo.RelationshipVo;
import com.lovbe.icharge.common.util.fallback.BaseFallback;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@Component
public class SocialServiceFallback extends BaseFallback implements SocialService {

    @Override
    public ResponseBean<List<RelationshipVo>> getRelationshipList(@RequestBody BaseRequest<List<Long>>  userIds,
                                                                  @RequestHeader("userId") Long userId) {
        return getFallBack();
    }

    @Override
    public ResponseBean<List<Long>> getFollowUserList(Long userId) {
        return getFallBack();
    }
}
