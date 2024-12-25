package com.lovbe.icharge.service;

import com.lovbe.icharge.entity.dto.BrowseHistoryDo;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/26 1:02
 * @Description: MS
 */
public interface ActionHandlerService {

    void handlerBrowseAction(List<BrowseHistoryDo> collect);
}
