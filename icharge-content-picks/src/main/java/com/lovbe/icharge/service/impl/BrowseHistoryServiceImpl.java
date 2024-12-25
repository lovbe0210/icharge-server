package com.lovbe.icharge.service.impl;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.dao.BrowseHistoryDao;
import com.lovbe.icharge.entity.dto.BrowseHistoryDTO;
import com.lovbe.icharge.entity.vo.BrowseHistoryVo;
import com.lovbe.icharge.service.BrowseHistoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/25 20:16
 * @Description: MS
 */
@Service
public class BrowseHistoryServiceImpl implements BrowseHistoryService {
    @Resource
    private BrowseHistoryDao browseHistoryDao;

    @Override
    public List<BrowseHistoryVo> getBrowseHistory(BaseRequest<RequestListDTO> request, Long userId) {
        List<BrowseHistoryDTO> browseHistoryList = browseHistoryDao.selectHistoryList(request.getData(), userId);
        if (CollectionUtils.isEmpty(browseHistoryList)) {
            return List.of();
        }
        // 按日期分组然后排序
        List<BrowseHistoryVo> resultList = new ArrayList<>();
        browseHistoryList.stream()
                .collect(Collectors.groupingBy(BrowseHistoryDTO::getHistoryDate))
                .forEach((date, browseHistoryDTOS) -> {
                    BrowseHistoryVo historyVo = new BrowseHistoryVo().setHistoryDate(date);
                    browseHistoryDTOS.sort((o1, o2) -> o2.getUpdateTime().compareTo(o1.getUpdateTime()));
                    historyVo.setList(browseHistoryDTOS);
                });
        return resultList;
    }
}
