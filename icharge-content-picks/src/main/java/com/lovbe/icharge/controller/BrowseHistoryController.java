package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.entity.vo.BrowseHistoryVo;
import com.lovbe.icharge.service.BrowseHistoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/25 13:28
 * @Description: 浏览记录
 */
@RestController
public class BrowseHistoryController {
    @Resource
    private BrowseHistoryService browseHistoryService;
    /**
     * @description: 获取历史记录
     * @param:
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/25 20:18
     */
    @PostMapping("/history")
    public ResponseBean getBrowseHistory(@RequestBody BaseRequest<RequestListDTO> request,
                                         @RequestHeader("userId") Long userId) {
        List<BrowseHistoryVo> historyList = browseHistoryService.getBrowseHistory(request, userId);
        return ResponseBean.ok(historyList);
    }
}
