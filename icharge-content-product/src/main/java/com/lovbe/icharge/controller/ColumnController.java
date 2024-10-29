package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.ArticleDTO;
import com.lovbe.icharge.entity.dto.CreateColumnDTO;
import com.lovbe.icharge.entity.vo.ArticleVO;
import com.lovbe.icharge.entity.vo.ColumnVo;
import com.lovbe.icharge.service.ColumnService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * description: 创建专栏
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param columnDTO
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/column/create")
    public ResponseBean<ArticleVO> createColumn(@RequestBody @Valid BaseRequest<CreateColumnDTO> columnDTO,
                                                 @RequestHeader("userId") long userId) {

        ColumnVo columnVo = columnService.createColumn(columnDTO.getData(), userId);
        return ResponseBean.ok(columnVo);
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
    public ResponseBean<ArticleVO> getColumnForEdit(@PathVariable("columnId") Long columnId,
                                                    @RequestHeader("userId") long userId) {

        ColumnVo columnVo = columnService.getColumnForEdit(columnId, userId);
        return ResponseBean.ok(columnVo);
    }

    /**
     * description: 获取专栏信息
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param columnId
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PutMapping("/column/update")
    public ResponseBean<ArticleVO> updateColumnInfo(@PathVariable("columnId") Long columnId,
                                                    @RequestHeader("userId") long userId) {

        ColumnVo columnVo = columnService.getColumnForEdit(columnId, userId);
        return ResponseBean.ok(columnVo);
    }
}
