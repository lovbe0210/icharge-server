package com.lovbe.icharge.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.ColumnDTO;
import com.lovbe.icharge.entity.dto.ColumnOperateDTO;
import com.lovbe.icharge.entity.dto.CreateColumnDTO;
import com.lovbe.icharge.entity.vo.ArticleVO;
import com.lovbe.icharge.entity.vo.ColumnVo;
import com.lovbe.icharge.service.ArticleService;
import com.lovbe.icharge.service.ColumnService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    @PostMapping("/columnList")
    public ResponseBean<List<ColumnVo>> getColumnList(@RequestHeader("userId") long userId) {
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
                                               @RequestHeader("userId") long userId) {

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
    @PostMapping("/column/createArticle")
    public ResponseBean<ArticleVO> createArticle(@RequestBody @Valid BaseRequest<ColumnDTO> request,
                                                 @RequestHeader("userId") long userId) {
        ArticleVO article = articleService.createBlankDoc(request.getData().getUid(), userId);
        return ResponseBean.ok(article);
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
    public ResponseBean<ArticleVO> deleteColumnInfo(@RequestBody @Valid BaseRequest<ColumnDTO> columnRequest,
                                                    @RequestHeader("userId") long userId) {
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
                                                   @RequestHeader("userId") long userId) {

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
    public ResponseBean<ArticleVO> getColumnDir(@PathVariable("columnId") Long columnId,
                                                @RequestHeader("userId") long userId) {
        JSONArray columnDir = columnService.getColumnDir(columnId, userId);
        return ResponseBean.ok(columnDir);
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
    public ResponseBean<ArticleVO> updateColumnInfo(@Validated ColumnDTO columnDTO,
                                                    @RequestHeader("userId") long userId) {
        MultipartFile coverFile = columnDTO.getCoverFile();
        if (coverFile != null && coverFile.getSize() > SysConstant.SIZE_10MB) {
            throw new ServiceException(ServiceErrorCodes.FILE_OUT_SIZE_10);
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
    public ResponseBean<ArticleVO> updateColumnDir(@RequestBody @Valid BaseRequest<ColumnDTO> request,
                                                   @RequestHeader("userId") long userId) {
        columnService.updateColumnDir(request.getData(), userId);
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
    @PostMapping("/column/batchOperate")
    public ResponseBean<ArticleVO> batchOperate(@RequestBody @Valid BaseRequest<ColumnOperateDTO> columnRequest,
                                                @RequestHeader("userId") long userId) {
        columnService.batchOperate(columnRequest, userId);
        return ResponseBean.ok();
    }
}
