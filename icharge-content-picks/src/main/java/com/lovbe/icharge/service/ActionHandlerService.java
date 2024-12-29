package com.lovbe.icharge.service;

import com.lovbe.icharge.entity.dto.BrowseHistoryDo;
import com.lovbe.icharge.entity.dto.CollectActionDTO;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/26 1:02
 * @Description: 用户交互消息消费
 */
public interface ActionHandlerService {
    /**
     * @description: 浏览记录
     * @param: List<BrowseHistoryDo>
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/29 17:06
     */
    void handlerBrowseAction(List<BrowseHistoryDo> collect);
    /**
     * @description: 收藏记录
     * @param: List<CollectActionDTO>
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/29 17:07
     */
    void handlerCollectAction(List<CollectActionDTO> collect);
}
