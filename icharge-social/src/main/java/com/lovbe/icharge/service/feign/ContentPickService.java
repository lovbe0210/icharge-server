package com.lovbe.icharge.service.feign;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.FileUploadDTO;
import com.lovbe.icharge.common.model.vo.PublicArticleVo;
import com.lovbe.icharge.common.model.vo.RamblyJotVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "content-picks", fallback = ContentPickServiceFallback.class)
public interface ContentPickService {

    /**
     * description: 通过id批量获取随笔列表
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/api/cps/ramblyjot/ids")
    ResponseBean<List<RamblyJotVo>> getRamblyjotListByIds(@RequestBody BaseRequest<List<Long>> baseRequest,
                                                    @RequestHeader(value = SysConstant.USERID) Long userId);

    /**
     * description: 通过id批量获取文章列表
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/api/cps/article/ids")
    ResponseBean<List<PublicArticleVo>> getArticleListByIds(@RequestBody BaseRequest<List<Long>> baseRequest,
                                                      @RequestHeader(value = SysConstant.USERID) Long userId);
}
