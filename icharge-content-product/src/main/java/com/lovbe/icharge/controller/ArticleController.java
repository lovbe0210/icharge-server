package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.entity.dto.ArticleDTO;
import com.lovbe.icharge.entity.dto.ContentDTO;
import com.lovbe.icharge.entity.vo.ArticleVO;
import com.lovbe.icharge.entity.vo.ContentVO;
import com.lovbe.icharge.service.ArticleService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/9 23:46
 * @Description: 文档相关controller
 */
@RestController
public class ArticleController {
    @Resource
    private ArticleService articleService;

    /**
     * description: 创建空白文档
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/createBlankDoc")
    public ResponseBean<ArticleVO> createBlankDoc(@RequestHeader("userId") long userId) {
        return ResponseBean.ok(articleService.createBlankDoc(userId));
    }

    /**
     * description: 获取文档信息用于编辑
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/article/{articleId}")
    public ResponseBean<ArticleVO> getArticleForEdit(@PathVariable("articleId") String articleId,
                                                     @RequestHeader("userId") long userId) {
        return ResponseBean.ok(articleService.getArticleForEdit(articleId, userId));
    }

    /**
     * description: 更新文档信息(包含封面文件)
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param articleDTO
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PutMapping("/article/update")
    public ResponseBean<ArticleVO> updateArticle(@Validated ArticleDTO articleDTO,
                                                 @RequestHeader("userId") long userId) {
        MultipartFile coverFile = articleDTO.getCoverFile();
        if (coverFile != null && coverFile.getSize() > SysConstant.SIZE_10MB) {
            throw new ServiceException(ServiceErrorCodes.FILE_OUT_SIZE_10);
        }
        articleService.updateArticle(false, articleDTO, userId);
        return ResponseBean.ok();
    }

    /**
     * description: 更新文档信息
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param baseRequest
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/article/simpleUpdate")
    public ResponseBean<ArticleVO> simpleUpdateArticle(@RequestBody @Valid BaseRequest<ArticleDTO> baseRequest,
                                                 @RequestHeader("userId") long userId) {
        articleService.updateArticle(true, baseRequest.getData(), userId);
        return ResponseBean.ok();
    }

    /**
     * description: 获取文档内容
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param articleId
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/content/{articleId}")
    public ResponseBean<ArticleVO> getContent(@PathVariable("articleId") Long articleId,
                                              @RequestHeader("userId") long userId) {
        ContentVO content = articleService.getContent(articleId, userId);
        return ResponseBean.ok(content);
    }

    /**
     * description: 更新文档内容
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param contentEntity
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/article/content")
    public ResponseBean<ArticleVO> updateContent(@RequestBody @Valid BaseRequest<ContentDTO> contentEntity,
                                                 @RequestHeader("userId") long userId) {
        Map content = articleService.updateContent(contentEntity, userId);
        return ResponseBean.ok(content);
    }

    /**
     * description: 获取文档列表
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param requestDto
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/articleList")
    public ResponseBean<ArticleVO> getMyArticleList(@RequestBody @Valid BaseRequest<RequestListDTO> requestDto,
                                                 @RequestHeader("userId") long userId) {
        List<ArticleVO> articleList = articleService.getMyArticleList(requestDto, userId);
        return ResponseBean.ok(articleList);
    }

    /**
     * description: 文章置顶/取消置顶
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param requestDto
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/article/setTop")
    public ResponseBean<ArticleVO> updateArticleTop(@RequestBody @Valid BaseRequest<ArticleDTO> requestDto,
                                                    @RequestHeader("userId") long userId) {
        articleService.updateArticleTop(requestDto, userId);
        return ResponseBean.ok();
    }

    /**
     * description: 文章发布
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param articleId
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/article/publish/{articleId}")
    public ResponseBean<ArticleVO> publishArticle(@PathVariable("articleId") Long articleId,
                                                  @RequestHeader("userId") long userId) {
        articleService.publishArticle(articleId, userId);
        return ResponseBean.ok();
    }

    /**
     * description: 文章删除
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param articleId
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/article/delete/{articleId}")
    public ResponseBean<ArticleVO> deleteArticle(@PathVariable("articleId") Long articleId,
                                                  @RequestHeader("userId") long userId) {
        articleService.deleteArticle(articleId, userId);
        return ResponseBean.ok();
    }
}
