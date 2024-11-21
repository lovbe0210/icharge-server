package com.lovbe.icharge.dao;

import com.lovbe.icharge.common.model.dto.ArticleDo;
import com.lovbe.icharge.common.model.dto.ContentDo;
import com.lovbe.icharge.entity.RouterInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/19 1:29
 * @Description: MS
 */
@Mapper
public interface PublicContentDao {
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
}
