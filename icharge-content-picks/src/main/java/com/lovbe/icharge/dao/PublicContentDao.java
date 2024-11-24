package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.ArticleDo;
import com.lovbe.icharge.common.model.dto.ColumnDo;
import com.lovbe.icharge.common.model.dto.ContentDo;
import com.lovbe.icharge.entity.PublicArticleVo;
import com.lovbe.icharge.entity.RouterInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/19 1:29
 * @Description: MS
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
                    SELECT * FROM c_article WHERE uri = #{uri} AND status = 'A';
                    """)
    ArticleDo selectArticleInfo(@Param("uri") String articleUri);

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
    List<ArticleDo> selectArticleList(@Param("authorId") Long authorId);

    /**
     * @description 获取专栏信息
     * @param[1] uri
     * @return ColumnDo
     * @author lovbe0210
     * @date 2024/11/23 22:36
     */
    ColumnDo selectColumnInfo(@Param("uri") String uri);
}
