package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.ArticleDo;
import com.lovbe.icharge.entity.dto.ContentPublishDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;


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
    @Update(value = """
                    UPDATE c_article
                    SET sort = CASE WHEN sort IS NULL 
                                    THEN(SELECT * FROM (SELECT IFNULL(MAX(sort), 0)+1 FROM c_article) t1) 
                                    ELSE NULL
                               END,
                        update_time = (SELECT * FROM (SELECT update_time FROM c_article WHERE uid = #{uid}) t2)
                    WHERE uid = #{uid};
                    """)
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
}
