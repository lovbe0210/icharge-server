package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.FileUploadDTO;
import com.lovbe.icharge.common.model.vo.PublicArticleVo;
import com.lovbe.icharge.common.model.vo.RamblyJotVo;
import com.lovbe.icharge.common.model.vo.RecommendColumnVo;
import com.lovbe.icharge.common.util.fallback.BaseFallback;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class ContentPickServiceFallback extends BaseFallback implements ContentPickService {

    @Override
    public ResponseBean<List<RamblyJotVo>> getRamblyjotListByIds(BaseRequest<Collection<Long>> baseRequest, Long userId) {
        return getFallBack();
    }

    @Override
    public ResponseBean<List<PublicArticleVo>> getArticleListByIds(BaseRequest<Collection<Long>> baseRequest, Long userId) {
        return getFallBack();
    }

    @Override
    public ResponseBean<List<RecommendColumnVo>> getColumnListByIds(BaseRequest<Collection<Long>> baseRequest, Long userId) {
        return getFallBack();
    }
}
