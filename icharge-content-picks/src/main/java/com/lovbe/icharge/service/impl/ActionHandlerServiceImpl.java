package com.lovbe.icharge.service.impl;

import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.dao.BrowseHistoryDao;
import com.lovbe.icharge.dao.CollectDao;
import com.lovbe.icharge.entity.dto.BrowseHistoryDo;
import com.lovbe.icharge.entity.dto.CollectActionDTO;
import com.lovbe.icharge.service.ActionHandlerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/26 1:03
 * @Description: MS
 */
@Service
public class ActionHandlerServiceImpl implements ActionHandlerService {
    @Resource
    private BrowseHistoryDao browseHistoryDao;
    @Resource
    private CollectDao collectDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handlerBrowseAction(List<BrowseHistoryDo> collect) {
        // 对同一批次中的同一uid进行去重,只算一次阅读
        Collection<BrowseHistoryDo> historyDos = collect.stream()
                .collect(Collectors.toMap(
                        BrowseHistoryDo::getUid,
                        Function.identity(),
                        (b1, b2) -> b1.getUpdateTime().after(b2.getUpdateTime()) ? b1 : b2))
                .values();
        // 对target进行分组，然后添加浏览记录统计
        List<TargetStatisticDo> statisticList = new ArrayList<>();
        historyDos.stream()
                .collect(Collectors.groupingBy(BrowseHistoryDo::getTargetId))
                .forEach((targetId, list) -> {
                    TargetStatisticDo statisticDo = new TargetStatisticDo()
                            .setViewCount(list.size())
                            .setType(list.get(0).getTargetType());
                    statisticDo.setUid(targetId);
                    statisticList.add(statisticDo);
                });
        browseHistoryDao.updateViewStatistic(statisticList);
        browseHistoryDao.insertOrUpdate(historyDos);
    }

    @Override
    public void handlerCollectAction(List<CollectActionDTO> collect) {
        // 对uid进行分组然后相加得到最终的收藏状态
        List<TargetStatisticDo> statisticList = new ArrayList<>();
        collect.stream()
                .peek(action -> {
                    // 在计算的时候转换为-1便于计总和
                    if (action.getAction() == 0) {
                        action.setAction(-1);
                    }
                })
                .collect(Collectors.groupingBy(CollectActionDTO::getUid))
                .forEach((uid, list) -> {
                    int count = list.stream().mapToInt(CollectActionDTO::getAction).sum();
                    TargetStatisticDo statisticDo = new TargetStatisticDo()
                            .setCollectCount(count)
                            .setType(list.get(0).getTargetType());
                    statisticDo.setUid(uid);
                    statisticList.add(statisticDo);
                });
        collectDao.updateCollectStatistic(statisticList);
    }
}
