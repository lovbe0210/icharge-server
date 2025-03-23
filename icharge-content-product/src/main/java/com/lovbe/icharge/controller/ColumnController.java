package com.lovbe.icharge.controller;

import cn.hutool.json.JSONArray;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.*;
import com.lovbe.icharge.entity.vo.ArticleVo;
import com.lovbe.icharge.entity.vo.ColumnVo;
import com.lovbe.icharge.service.ArticleService;
import com.lovbe.icharge.service.ColumnService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/29 22:16
 * @Description: 专栏操作controller
 */
@RestController
public class ColumnController {

    @Resource
    private ColumnService columnService;
    @Resource
    private ArticleService articleService;

    /**
     * description: 获取专栏列表
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ColumnVo>
     */
    @GetMapping("/column/list")
    public ResponseBean<List<ColumnVo>> getColumnList(@RequestHeader(SysConstant.USERID) long userId) {
        List<ColumnVo> columnList = columnService.getColumnList(userId);
        return ResponseBean.ok(columnList);
    }

    /**
     * description: 创建专栏
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param columnDTO
     * @param userId
     * @return ResponseBean<ColumnVo>
     */
    @PostMapping("/column/create")
    public ResponseBean<ColumnVo> createColumn(@RequestBody @Valid BaseRequest<CreateColumnDTO> columnDTO,
                                               @RequestHeader(SysConstant.USERID) long userId) {

        ColumnVo columnVo = columnService.createColumn(columnDTO.getData(), userId);
        return ResponseBean.ok(columnVo);
    }

    /**
     * description: 专栏新建文章
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param request
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
        @PostMapping("/column/article/create")
    public ResponseBean<ArticleVo> createArticle(@RequestBody @Valid BaseRequest<ColumnDTO> request,
                                                 @RequestHeader(SysConstant.USERID) long userId) {
        ArticleVo article = articleService.createBlankDoc(request.getData().getUid(), userId);
        return ResponseBean.ok(article);
    }

    /**
     * description: 专栏复制文章
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param requestDto
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/column/article/copy")
    public ResponseBean<ArticleVo> copyArticle(@RequestBody @Valid BaseRequest<ArticleOperateDTO> requestDto,
                                               @RequestHeader(SysConstant.USERID) long userId) {

        ArticleOperateDTO data = requestDto.getData();
        Assert.notNull(data.getColumnId(), "专栏id不得为空");
        Map<Long, ArticleVo> copyArticle = articleService.copyArticle(data, userId);
        return ResponseBean.ok(copyArticle);
    }

    /**
     * description: 专栏删除
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param columnRequest
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/column/delete")
    public ResponseBean<ArticleVo> deleteColumnInfo(@RequestBody @Valid BaseRequest<ColumnDTO> columnRequest,
                                                    @RequestHeader(SysConstant.USERID) long userId) {
        columnService.deleteColumnInfo(columnRequest.getData(), userId);
        return ResponseBean.ok();
    }

    /**
     * description: 获取专栏信息
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param columnId
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @GetMapping("/column/{columnId}")
    public ResponseBean<ColumnVo> getColumnForEdit(@PathVariable("columnId") Long columnId,
                                                   @RequestHeader(SysConstant.USERID) long userId) {

        ColumnVo columnVo = columnService.getColumnForEdit(columnId, userId);
        return ResponseBean.ok(columnVo);
    }

    /**
     * description: 获取专栏目录
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param columnId
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @GetMapping("/column/dir/{columnId}")
    public ResponseBean<ArticleVo> getColumnDir(@PathVariable("columnId") Long columnId,
                                                @RequestHeader(SysConstant.USERID) long userId) {
        JSONArray columnDir = columnService.getColumnDir(columnId, userId);
        return ResponseBean.ok(columnDir);
    }

    /**
     * description: 获取专栏文章
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param columnId
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @GetMapping("/column/article/list/{columnId}")
    public ResponseBean<List<ArticleVo>> getColumnArticleList(@PathVariable("columnId") Long columnId,
                                                              @RequestHeader(SysConstant.USERID) long userId) {
        List<ArticleVo> articleList = columnService.getColumnArticleList(columnId, userId);
        return ResponseBean.ok(articleList);
    }


    /**
     * description: 编辑专栏信息
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param columnDTO
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PutMapping("/column/update")
    public ResponseBean<ArticleVo> updateColumnInfo(@Validated ColumnDTO columnDTO,
                                                    @RequestHeader(SysConstant.USERID) long userId) {
        MultipartFile coverFile = columnDTO.getCoverFile();
        if (coverFile != null && coverFile.getSize() > SysConstant.SIZE_5MB) {
            throw new ServiceException(ServiceErrorCodes.FILE_OUT_SIZE_5);
        }
        columnService.updateColumn(columnDTO, userId);
        return ResponseBean.ok();
    }

    /**
     * description: 专栏目录更新
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param request
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/column/dir/update")
    public ResponseBean<ArticleVo> updateColumnDir(@RequestBody @Valid BaseRequest<ColumnDTO> request,
                                                   @RequestHeader(SysConstant.USERID) long userId) {
        Long dirContentId = columnService.updateColumnDir(request.getData(), userId);
        return ResponseBean.ok(dirContentId);
    }

    /**
     * @description: 专栏首页自定义内容更新
     * @param: String
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/14 23:38
     */
    @PostMapping("/column/content/update")
    public ResponseBean updateDomainContent(@RequestHeader(SysConstant.USERID)Long userId,
                                            @RequestBody @Valid BaseRequest<ContentDTO> contentEntity) {
        columnService.updateContent(contentEntity, userId);
        return ResponseBean.ok();
    }

    /**
     * description: 专栏内容批量操作
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param columnRequest
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/column/article/operate/batch")
    public ResponseBean<ArticleVo> batchOperate(@RequestBody @Valid BaseRequest<ColumnOperateDTO> columnRequest,
                                                @RequestHeader(SysConstant.USERID) long userId) {
        columnService.batchOperate(columnRequest, userId);
        return ResponseBean.ok();
    }
}
