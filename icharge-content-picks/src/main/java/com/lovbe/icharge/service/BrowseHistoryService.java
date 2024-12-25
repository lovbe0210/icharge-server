package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.entity.vo.BrowseHistoryVo;

import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/25 20:15
 * @Description: 历史记录
 */
public interface BrowseHistoryService {
    List<BrowseHistoryVo> getBrowseHistory(BaseRequest<RequestListDTO> request, Long userId);
}
