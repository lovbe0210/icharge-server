package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.RamblyJotDTO;
import com.lovbe.icharge.common.model.vo.RamblyJotVo;
import com.lovbe.icharge.service.RamblyJotService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * description: 新建随笔
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param baseRequest
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/ramblyjot/create")
    public ResponseBean<RamblyJotVo> createRamblyJot(@RequestBody @Valid BaseRequest<RamblyJotDTO> baseRequest,
                                                     @RequestHeader(SysConstant.USERID) long userId) {
        RamblyJotVo ramblyJotVo = ramblyJotService.createRamblyJot(baseRequest.getData(), userId);
        return ResponseBean.ok(ramblyJotVo);
    }

    /**
     * description: 更新随笔信息 只修改可见范围
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param baseRequest
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/ramblyjot/update")
    public ResponseBean<RamblyJotVo> updateRamblyJot(@RequestBody @Valid BaseRequest<RamblyJotDTO> baseRequest,
                                                     @RequestHeader(SysConstant.USERID) long userId) {
        RamblyJotDTO data = baseRequest.getData();
        Assert.notNull(data.getUid(), "随笔id不得为空");
        Assert.notNull(data.getIsPublic(), "可见范围状态不得为空");
        ramblyJotService.updateRamblyJot(data, userId);
        return ResponseBean.ok();
    }


    /**
     * description: 获取随笔列表
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/ramblyjot/list")
    public ResponseBean<List<RamblyJotVo>> getRamblyJotList(@RequestHeader(SysConstant.USERID) long userId) {
        List<RamblyJotVo> ramblyJotList = ramblyJotService.getRamblyJotList(userId);
        return ResponseBean.ok(ramblyJotList);
    }

    /**
     * description: 删除随笔
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @PostMapping("/ramblyjot/delete/{ramblyJotId}")
    public ResponseBean<List<RamblyJotVo>> deleteRamblyJot(@PathVariable("ramblyJotId") Long ramblyJotId,
                                                           @RequestHeader(SysConstant.USERID) long userId) {
        ramblyJotService.deleteRamblyJot(ramblyJotId, userId);
        return ResponseBean.ok();
    }
}
