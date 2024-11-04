package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.ColumnDTO;
import com.lovbe.icharge.entity.dto.CreateColumnDTO;
import com.lovbe.icharge.entity.vo.ArticleVO;
import com.lovbe.icharge.entity.vo.ColumnVo;
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
    @PostMapping("/column/{columnId}")
    public ResponseBean<ColumnVo> getColumnForEdit(@PathVariable("columnId") Long columnId,
                                                   @RequestHeader("userId") long userId) {

        ColumnVo columnVo = columnService.getColumnForEdit(columnId, userId);
        return ResponseBean.ok(columnVo);
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
}
