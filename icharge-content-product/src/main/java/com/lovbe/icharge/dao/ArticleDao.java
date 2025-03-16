package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.ArticleDo;
import com.lovbe.icharge.common.model.dto.SocialNoticeDo;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.entity.dto.ContentPublishDTO;
import com.lovbe.icharge.entity.dto.GrowthStatsDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


/**
 * 文档相关mapper
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 23:11
 */
@Mapper
public interface ArticleDao extends BaseMapper<ArticleDo> {
    /**
     * @description 文章置顶
     * @param[1] uid
     * @author lovbe0210
     * @date 2024/11/9 16:28
     */
    void updateArticleTop(@Param("uid") Long uid);

    /**
     * @description 批量操作
     * @param[1] collect
     * @param[2] columnId
     * @param[3] isPublic
     * @param[4] operateType
     * @author lovbe0210
     * @date 2024/11/9 16:28
     */
    void batchUpdate(@Param("articleList") List<ArticleDo> collect,
                     @Param("columnId") Long columnId,
                     @Param("isPublic") Integer isPublic,
                     @Param("operateType") String operateType);

    /**
     * @description: 修改发布状态
     * @param: ContentPublishDTO
     * @return: void
     * @author: lovbe0210
     * @date: 2024/12/31 13:51
     */
    int updateByPublishContent(@Param("publishDTO") ContentPublishDTO publishDTO,
                                @Param("publishStatus") int publishStatus);

    /**
     * @description: 获取能否自动发布文章
     * @param: uid
     * @return: int
     * @author: lovbe0210
     * @date: 2025/2/11 23:55
     */
    Integer selectEnableAutoPublish(@Param("uid") Long uid);

    /**
     * @description: 插入文章审核通知
     * @param: noticeDo
     * @author: lovbe0210
     * @date: 2025/3/16 9:55
     */
    void insertAuditNotice(@Param("noticeDo") SocialNoticeDo noticeDo);

    /**
     * @description: 获取昨日更新文章数
     * @param: userIds
     * @return: int
     * @author: lovbe0210
     * @date: 2025/3/16 9:59
     */
    List<TargetStatisticDo> selectYdUpdateArticleCount(@Param("userIds") Set<Long> userIds);
    /**
     * @description: 获取昨日更新专栏数
     * @param: userIds
     * @return: com.lovbe.icharge.common.model.dto.TargetStatisticDo
     * @author: lovbe0210
     * @date: 2025/3/16 10:33
     */
    List<TargetStatisticDo> selectYdUpdateColumnCount(@Param("userIds") Set<Long> userIds);
    /**
     * @description: 获取昨日更新随笔数
     * @param: userIds
     * @return: com.lovbe.icharge.common.model.dto.TargetStatisticDo
     * @author: lovbe0210
     * @date: 2025/3/16 10:33
     */
    List<TargetStatisticDo> selectYdUpdateEssayCount(@Param("userIds") Set<Long> userIds);

    /**
     * @description: 获取文章统计数据
     * @param: userIds
     * @return: java.util.List<com.lovbe.icharge.entity.dto.GrowthStatsDo>
     * @author: lovbe0210
     * @date: 2025/3/16 15:50
     */
    List<GrowthStatsDo> selectArticleCreationStatistic(@Param("userIds") Set<Long> userIds);

    /**
     * @description: 获取专栏统计数据
     * @param: userIds
     * @return: java.util.List<com.lovbe.icharge.entity.dto.GrowthStatsDo>
     * @author: lovbe0210
     * @date: 2025/3/16 16:06
     */
    List<GrowthStatsDo> selectColumnCreationStatistic(@Param("userIds") Set<Long> userIds);

    /**
     * @description:  获取随笔统计数据
     * @param: userIds
     * @return: java.util.List<com.lovbe.icharge.entity.dto.GrowthStatsDo>
     * @author: lovbe0210
     * @date: 2025/3/16 21:35
     */
    List<GrowthStatsDo> selectEssayCreationStatistic(@Param("userIds") Set<Long> userIds);
}
