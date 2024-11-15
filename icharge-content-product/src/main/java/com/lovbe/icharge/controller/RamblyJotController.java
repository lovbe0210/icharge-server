package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.entity.dto.ArticleDTO;
import com.lovbe.icharge.entity.dto.ArticleOperateDTO;
import com.lovbe.icharge.entity.dto.ContentDTO;
import com.lovbe.icharge.entity.dto.RamblyJotDTO;
import com.lovbe.icharge.entity.vo.ArticleVo;
import com.lovbe.icharge.entity.vo.ContentVo;
import com.lovbe.icharge.entity.vo.RamblyJotVo;
import com.lovbe.icharge.service.RamblyJotService;
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
 * @Description: 随笔相关controller
 */
@RestController
public class RamblyJotController {
    @Resource
    private RamblyJotService ramblyJotService;

    /**
     * description: 创建空白文档
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/ramblyjot/{ramblyJotId}")
    public ResponseBean<RamblyJotVo> getRamblyJotInfo(@PathVariable("articleId") Long ramblyJotId,
                                                      @RequestHeader("userId") long userId) {
        return ResponseBean.ok(ramblyJotService.getRamblyJotInfo(userId, ramblyJotId));
    }


    /**
     * description: 更新文档信息(包含封面文件)
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param baseRequest
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/ramblyjot/create")
    public ResponseBean<RamblyJotVo> createRamblyJot(@RequestBody @Valid BaseRequest<RamblyJotDTO> baseRequest,
                                                 @RequestHeader("userId") long userId) {
        RamblyJotVo ramblyJotVo = ramblyJotService.createRamblyJot(baseRequest.getData(), userId);
        return ResponseBean.ok(ramblyJotVo);
    }

    /**
     * description: 更新文档信息
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/ramblyjot/list")
    public ResponseBean<List<RamblyJotVo>> simpleUpdateArticle(@RequestHeader("userId") long userId) {
        List<RamblyJotVo> ramblyJotList = ramblyJotService.getRamblyJotList(userId);
        return ResponseBean.ok(ramblyJotList);
    }
}
