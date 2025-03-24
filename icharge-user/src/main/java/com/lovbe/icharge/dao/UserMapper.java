package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.ContentDo;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.entity.dto.DomainContentUpdateDTO;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 用户相关mapper
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 23:11
 */
@Mapper
public interface UserMapper extends BaseMapper<UserInfoDo> {

    /**
     * @description: 编辑个人主页内容
     * @param: contentUpdateDTO
     * @author: lovbe0210
     * @date: 2025/2/5 18:01
     */
    @Insert(value = """
                    INSERT INTO c_content(uid, content, update_time) 
                    VALUES (#{contentUpdateDTO.contentId}, #{contentUpdateDTO.content}, #{contentUpdateDTO.updateTime})
                    ON DUPLICATE KEY UPDATE
                    content = #{contentUpdateDTO.content},
                    update_time = #{contentUpdateDTO.updateTime};
                    """)
    void updateDomainContent(@Param("contentUpdateDTO") DomainContentUpdateDTO contentUpdateDTO);

    /**
     * @description: 获取个人主页内容
     * @param: contentId
     * @return: java.lang.Object
     * @author: lovbe0210
     * @date: 2025/2/5 18:01
     */
    @Select(value = """
                    SELECT content FROM c_content WHERE uid = #{contentId} AND status = 'A';
                    """)
    Object getDomainContent(@Param("contentId") Long contentId);

    /**
     * @description: 删除个人主页内容
     * @param: contentId
     * @author: lovbe0210
     * @date: 2025/2/6 14:31
     */
    @Update(value = """
                    UPDATE c_content set status = 'D' WHERE uid = #{contentId};
                    """)
    void deleteDomainContent(@Param("contentId") Long contentId);
    
    /** 
     * @description: 获取用户统计信息 
     * @param: userId 
     * @return: com.lovbe.icharge.common.model.dto.TargetStatisticDo
     * @author: lovbe0210
     * @date: 2025/3/14 0:25
     */
    @Select(value = """
                    SELECT
                    	IFNULL(sis.follow_count,0) followCount,
                    	IFNULL(sis.fans_count,0) fansCount,
                    	IFNULL(pgs.article_total,0) articleCount,
                    	IFNULL(pgs.column_total,0) columnCount
                    FROM `s_interaction_statistic` sis
                    LEFT JOIN p_growth_stats pgs ON pgs.user_id = #{userId}
                    	AND pgs.`status` = 'A'
                    	AND pgs.range_type = 1
                    WHERE sis.uid = #{userId}
                    	AND sis.`status` = 'A'
                    """)
    TargetStatisticDo selectStatisticInfo(@Param("userId") Long userId);

    /**
     * @description: 获取个人主页内容
     * @param: contentIdList
     * @return: java.util.List<com.lovbe.icharge.common.model.dto.ContentDo>
     * @author: lovbe0210
     * @date: 2025/3/24 17:00
     */
    @Select(value = """
                    <script>
                        SELECT * 
                        FROM c_content
                        WHERE status = 'A'
                        AND uid IN <foreach collection="contentIdList" item="contentId" open="(" close=")" separator=",">
                                       #{contentId}
                                   </foreach> 
                    </script>
                    """)
    List<ContentDo> selectDomainContentList(@Param("contentIdList") List<Long> contentIdList);
}
