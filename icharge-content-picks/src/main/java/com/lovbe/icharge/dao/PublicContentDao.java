package com.lovbe.icharge.dao;

import com.lovbe.icharge.common.model.dto.*;
import com.lovbe.icharge.entity.vo.FeaturedArticleVo;
import com.lovbe.icharge.entity.vo.RecommendColumnVo;
import com.lovbe.icharge.entity.vo.RouterInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/19 1:29
 * @Description: 公开内容Dao
 */
@Mapper
public interface PublicContentDao{
    /**
     * @description 根据uri获取文档信息
     * @param[1] articleUri
     * @return ArticleDo
     * @author lovbe0210
     * @date 2024/11/19 1:48
     */
    @Select(value = """
                    SELECT c.*, 
                           ci.uid collect_id,
                           cis.like_count likeCount,
                           cis.comment_count commentCount,
                           cis.collect_count collectCount,
                           cis.view_count viewCount
                    FROM c_article c
                    LEFT JOIN s_interaction_statistic cis ON c.uid = cis.uid
                    LEFT JOIN p_collect_item ci ON c.uid = ci.target_id 
                        AND ci.user_id = #{userId}
                        AND ci.status = 'A'
                    WHERE c.uri = #{uri} 
                        AND c.status = 'A';
                    """)
    ArticleDo selectArticleInfo(@Param("uri") String articleUri, @Param("userId") Long userId);

    /**
     * @description 获取正文内容
     * @param[1] latestContentId
     * @return ContentDo
     * @author lovbe0210
     * @date 2024/11/19 1:49
     */
    @Select(value = """
                    SELECT * FROM c_content WHERE uid = #{contentId} AND status = 'A';
                    """)
    ContentDo selectContent(@Param("contentId") Long latestContentId);

    /**
     * @description 根据uri判断是文章还是专栏
     * @param[1] dynamicId
     * @return RouterInfoVo
     * @author lovbe0210
     * @date 2024/11/22 23:06
     */
    @Select(value = """
                    SELECT min(type) type, userId FROM 
                    (SELECT 1 AS type, user_id AS userId 
                     FROM c_article 
                     WHERE status = 'A' 
                       AND uri = #{dynamicId}
                    UNION ALL
                    SELECT 2 AS type, user_id AS userId  
                    FROM c_column 
                    WHERE status = 'A' 
                      AND uri = #{dynamicId}) result
                    """)
    RouterInfoVo selectUriType(@Param("dynamicId") String dynamicId);

    /**
     * @description 获取文章列表
     * @param[1] authorId
     * @return List<PublicArticleVo>
     * @author lovbe0210
     * @date 2024/11/22 23:07
     */
    @Select(value = """
                    SELECT *        
                    FROM c_article
                    WHERE user_id = #{authorId}
                        AND column_id IS NULL
                        AND status = 'A'
                    ORDER BY sort DESC, update_time DESC
                    """)
    List<ArticleDo> selectArticleListByUserId(@Param("authorId") Long authorId);

    /**
     * @description 获取文章列表
     * @param[1] articleIds
     * @return List<ArticleDo>
     * @author lovbe0210
     * @date 2024/12/1 14:30
     */
    List<ArticleDo> selectArticleList(@Param("articleIds") Collection<Long> articleIds);

    /**
     * @description 获取专栏信息
     * @param[1] uri
     * @return ColumnDo
     * @author lovbe0210
     * @date 2024/11/23 22:36
     */
    ColumnDo selectColumnInfo(@Param("uri") String uri);

    /**
     * @description 获取专栏列表
     * @param[1] columnIds
     * @return List<ColumnDo>
     * @author lovbe0210
     * @date 2024/12/1 14:39
     */
    List<ColumnDo> selectColumnList(@Param("columnIds") List<Long> columnIds);

    /**
     * @description 批量获取用户信息
     * @param[1] userIds
     * @return List<UserInfoDo>
     * @author lovbe0210
     * @date 2024/12/1 15:25
     */
    List<UserInfoDo> selectUserList(@Param("userIds") Collection<Long> userIds);

    /**
     * @description: 获取推荐列表
     * @param: RecommendRequestDTO
     * @return: List<RecommendArticleVo>
     * @author: lovbe0210
     * @date: 2024/12/27 13:50
     */
    List<FeaturedArticleVo> selectPublicArticleList(@Param("articleIds") Collection<Long> articleIdList);

    /**
     * @description: 获取统计列表
     * @param: String
     * @return: List<TargetStatisticDo>
     * @author: lovbe0210
     * @date: 2024/12/29 13:18
     */
    Integer selectStatisticCount(@Param("targetType") Integer targetType);
    List<TargetStatisticDo> selectArticleStatisticList(@Param("targetType") Integer targetType,
                                                       @Param("offset") Integer offset,
                                                       @Param("batchSize") Integer articleRankBatchSize);
    List<TargetStatisticDo> selectColumnStatisticList (@Param("targetType") Integer targetType,
                                                       @Param("offset") Integer offset,
                                                       @Param("batchSize") Integer articleRankBatchSize);

    /**
     * @description: 获取所有专栏文章列表
     * @param:
     * @return: List<ColumnDo>
     * @author: lovbe0210
     * @date: 2025/1/8 14:36
     */
    Integer selectColumnCount();
    List<TargetStatisticDo> selectColumnArticleList(@Param("offset") Integer offset,
                                           @Param("batchSize") Integer columnStatisticBatchSize);

    /**
     * @description: 更新专栏统计信息
     * @param: List<TargetStatisticDo>
     * @return: void
     * @author: lovbe0210
     * @date: 2025/1/8 14:36
     */
    void updateColumnStatistic(@Param("statisticList") List<TargetStatisticDo> statisticList);

    /**
     * @description: 获取公开专栏信息
     * @param: List<Long>
     * @return: List<RecommendColumnVo>
     * @author: lovbe0210
     * @date: 2025/1/9 16:57
     */
    List<RecommendColumnVo> selectPublicColumnList(@Param("columnIds") List columnIds);
}
