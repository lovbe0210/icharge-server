package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.entity.dto.LikeActionDo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 22:48
 * @Description: 社交点赞
 */
@Mapper
public interface SocialLikeDao extends BaseMapper<LikeActionDo> {
    /** 
     * @description: 点赞统计表更新
     * @param: Long
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/11 23:35
     */
    void updateStatisticByAdd(@Param("likeActionList") List<LikeActionDo> likeActionList);
    void updateStatisticBySub(@Param("likeActionList") List<LikeActionDo> likeActionList);

    /**
     * @description: 根据点赞对象获取数据list
     * @param: List<LikeActionDo>
     * @return: List<LikeActionDo>
     * @author: lovbe0210
     * @date: 2024/12/12 13:48
     */
    List<LikeActionDo> selectListByAction(@Param("actionList") List<LikeActionDo> actionList);
}
