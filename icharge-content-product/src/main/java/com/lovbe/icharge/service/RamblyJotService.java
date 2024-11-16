package com.lovbe.icharge.service;

import com.lovbe.icharge.entity.dto.RamblyJotDTO;
import com.lovbe.icharge.entity.vo.RamblyJotVo;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/15 12:47
 * @Description: 随笔相关service
 */
public interface RamblyJotService {
    /**
     * @description 获取随笔信息
     * @param[1] userId
     * @param[2] ramblyJotId
     * @return RamblyJotVo
     * @author lovbe0210
     * @date 2024/11/15 13:13
     */
    RamblyJotVo getRamblyJotInfo(long userId, Long ramblyJotId);

    /**
     * 创建随笔
     * @param data
     * @param userId
     * @return
     */
    RamblyJotVo createRamblyJot(RamblyJotDTO data, long userId);

    /**
     * @description 获取随笔列表
     * @param[1] userId
     * @return List<RamblyJotVo>
     * @author lovbe0210
     * @date 2024/11/15 15:36
     */
    List<RamblyJotVo> getRamblyJotList(long userId);

    /**
     * @description 删除随笔
     * @param[1] ramblyJotId
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/11/15 22:27
     */
    void deleteRamblyJot(Long ramblyJotId, long userId);

    /**
     * @description 更新随笔信息
     * @param[1] data
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/11/16 15:39
     */
    void updateRamblyJot(RamblyJotDTO data, long userId);
}
