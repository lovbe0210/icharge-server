package com.lovbe.icharge.dao;

import com.lovbe.icharge.common.model.dto.*;
import com.lovbe.icharge.entity.dto.GlobalSearchDTO;
import com.lovbe.icharge.entity.dto.RecommendRequestDTO;
import com.lovbe.icharge.entity.vo.CreateRecordVo;
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
    ArticleDo selectArticleInfo(@Param("uri") String articleUri, @Param("userId") Long userId);

    /**
     * @description: 获取随笔信息
     * @param: ramblyJotId
     * @return: com.lovbe.icharge.common.model.dto.RamblyJotDo
     * @author: lovbe0210
     * @date: 2025/1/23 23:11
     */
    RamblyJotDo getRamblyJotInfo(@Param("ramblyJotId") Long ramblyJotId);

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
    List<RecommendColumnVo> selectPublicColumnList(@Param("columnIds") Collection columnIds);

    /**
     * @description: 获取公开专栏信息
     * @param: RecommendRequestDTO
     * @return: List<RecommendColumnVo>
     * @author: lovbe0210
     * @date: 2025/1/10 17:16
     */
    List<RecommendColumnVo> selectPagePublicColumnList(@Param("data") RecommendRequestDTO data);

    /**
     * @description: 获取用户总数
     * @param:
     * @return: Integer
     * @author: lovbe0210
     * @date: 2025/1/11 16:36
     */
    @Select(value = """
                    SELECT COUNT(*) FROM p_user WHERE status = 'A';
                    """)
    Integer selectUserCount();
    List<TargetStatisticDo> selectUserStatisticList(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * @description:  获取用户相关统计数据
     * @param: collect
     * @return: java.util.List<com.lovbe.icharge.common.model.dto.TargetStatisticDo>
     * @author: lovbe0210
     * @date: 2025/1/22 1:59
     */
    List<TargetStatisticDo> selectUserStatisticListByIds(@Param("userIds") List<Long> collect);

    /**
     * @description: 获取菜单列表
     * @param:
     * @return: List<MenuDTO>
     * @author: lovbe0210
     * @date: 2025/1/12 17:23
     */
    @Select(value = """
                    SELECT * FROM t_menu WHERE status = 'A'   
                    """)
    List<MenuDTO> selecctMenuList();

    /**
     * @description: 通过userId或者columnId获取公开文章列表
     * @param: columnId
     * @return: java.util.List<com.lovbe.icharge.entity.vo.FeaturedArticleVo>
     * @author: lovbe0210
     * @date: 2025/1/15 19:56
     */
    List<FeaturedArticleVo> selectArticleListByTarget(@Param("searchDTO") GlobalSearchDTO searchDTO,
                                                      @Param("userId") Long userId);

    /**
     * @description: 获取公开随笔信息
     * @param: requestData
     * @return: java.util.List<com.lovbe.icharge.common.model.dto.RamblyJotDo>
     * @author: lovbe0210
     * @date: 2025/1/26 23:43
     */
    List<RamblyJotDo> selectPublicEssayList(@Param("requestData") RequestListDTO requestData);

    /**
     * @description: 获取个人主页公开专栏
     * @param: data
     * @return: java.util.List<com.lovbe.icharge.entity.vo.RecommendColumnVo>
     * @author: lovbe0210
     * @date: 2025/2/6 16:47
     */
    List<RecommendColumnVo> selectDomainPublicColumn(@Param("data") RecommendRequestDTO data);

    /** 
     * @description: 通过id查找随笔列表
     * @param: data 
     * @return: java.util.List<com.lovbe.icharge.common.model.dto.RamblyJotDo>
     * @author: lovbe0210
     * @date: 2025/2/24 20:32
     */
    List<RamblyJotDo> getRamblyjotListByIds(@Param("data") List<Long> data);

    /**
     * @description: 获取关注用户动态
     * @param: data
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.entity.vo.CreateRecordVo>
     * @author: lovbe0210
     * @date: 2025/3/13 0:09
     */
    List<CreateRecordVo> getCreateRecord(@Param("data") RecommendRequestDTO data,
                                         @Param("userIds") List<Long> userIds);
}
