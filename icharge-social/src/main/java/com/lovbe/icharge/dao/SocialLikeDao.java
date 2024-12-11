package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.entity.dto.LikeActionDo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    @Insert(value = """
                    INSERT INTO c_interaction_statistic (uid, type, like_count) 
                    VALUES (#{uid}, #{targetType}, 1)
                    ON DUPLICATE KEY UPDATE
                    like_count = like_count + 1
                    """)
    void updateStatisticByAdd(@Param("uid") Long targetId, @Param("targetType") int targetType);
    @Insert(value = """
                    UPDATE c_interaction_statistic set like_count = 
                    IF(like_count = 0, 0, like_count -1)
                    WHERE uid = #{uid} 
                    """)
    void updateStatisticBySub(@Param("uid") Long targetId);
}
