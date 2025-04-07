package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ErrorCode;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.entity.dto.ArticleDTO;
import com.lovbe.icharge.entity.dto.ArticleOperateDTO;
import com.lovbe.icharge.entity.dto.ContentDTO;
import com.lovbe.icharge.entity.dto.ContentLatexDTO;
import com.lovbe.icharge.entity.vo.ArticleVo;
import com.lovbe.icharge.entity.vo.ContentVo;
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
    @PostMapping("/createdoc")
    public ResponseBean<ArticleVo> createBlankDoc(@RequestHeader(SysConstant.USERID) long userId) {
        return ResponseBean.ok(articleService.createBlankDoc(null, userId));
    }

    /**
     * description: 获取文档信息用于编辑
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @GetMapping("/article/{uri}")
    public ResponseBean<ArticleVo> getArticleForEdit(@PathVariable("uri") String uri,
                                                     @RequestHeader(SysConstant.USERID) long userId) {
        return ResponseBean.ok(articleService.getArticleForEdit(uri, userId));
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
    public ResponseBean<ArticleVo> updateArticle(@Validated ArticleDTO articleDTO,
                                                 @RequestHeader(SysConstant.USERID) long userId) {
        MultipartFile coverFile = articleDTO.getCoverFile();
        if (coverFile != null && coverFile.getSize() > SysConstant.SIZE_5MB) {
            throw new ServiceException(ServiceErrorCodes.FILE_OUT_SIZE_5);
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
    @PostMapping("/article/update/simple")
    public ResponseBean<ArticleVo> simpleUpdateArticle(@RequestBody @Valid BaseRequest<ArticleDTO> baseRequest,
                                                       @RequestHeader(SysConstant.USERID) long userId) {
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
    @GetMapping("/article/content/{articleId}")
    public ResponseBean<ArticleVo> getContent(@PathVariable("articleId") Long articleId,
                                              @RequestHeader(SysConstant.USERID) long userId) {
        ContentVo content = articleService.getContent(articleId, userId);
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
    @PostMapping("/article/content/update")
    public ResponseBean<ArticleVo> updateContent(@RequestBody @Valid BaseRequest<ContentDTO> contentEntity,
                                                 @RequestHeader(SysConstant.USERID) long userId) {
        Map content = articleService.updateContent(contentEntity, userId);
        return ResponseBean.ok(content);
    }

    /**
     * @description: 数学公式转svg
     * @param: contentEntity
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.entity.vo.ArticleVo>
     * @author: lovbe0210
     * @date: 2025/4/5 0:42
     */
    @PostMapping("/content/math")
    public ResponseBean<ArticleVo> latex2Svg(@RequestBody @Valid ContentLatexDTO contentLatex,
                                             @RequestHeader(SysConstant.USERID) long userId) {
        String svgBase64 = articleService.latex2Img(contentLatex);
        return svgBase64 == null ?
                ResponseBean.error(ServiceErrorCodes.LATEX_2_SVG_FAILED.getCode(), ServiceErrorCodes.LATEX_2_SVG_FAILED.getMsg())
                : ResponseBean.ok(svgBase64);
    }

    /**
     * description: 获取文档列表
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param requestDto
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/article/list")
    public ResponseBean<ArticleVo> getMyArticleList(@RequestBody @Valid BaseRequest<RequestListDTO> requestDto,
                                                    @RequestHeader(SysConstant.USERID) long userId) {
        List<ArticleVo> articleList = articleService.getMyArticleList(requestDto, userId);
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
    @PostMapping("/article/setop")
    public ResponseBean<ArticleVo> updateArticleTop(@RequestBody @Valid BaseRequest<ArticleDTO> requestDto,
                                                    @RequestHeader(SysConstant.USERID) long userId) {
        articleService.updateArticleTop(requestDto, userId);
        return ResponseBean.ok();
    }

    /**
     * description: 文章批量操作
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param requestDto
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/article/operate/batch")
    public ResponseBean<ArticleVo> articleBatchOperate(@RequestBody @Valid BaseRequest<ArticleOperateDTO> requestDto,
                                                       @RequestHeader(SysConstant.USERID) long userId) {
        articleService.articleBatchOperate(requestDto, userId);
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
    public ResponseBean<ArticleVo> publishArticle(@PathVariable("articleId") Long articleId,
                                                  @RequestHeader(SysConstant.USERID) long userId) {
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
    public ResponseBean<ArticleVo> deleteArticle(@PathVariable("articleId") Long articleId,
                                                 @RequestHeader(SysConstant.USERID) long userId) {
        articleService.deleteArticle(articleId, userId);
        return ResponseBean.ok();
    }
}
