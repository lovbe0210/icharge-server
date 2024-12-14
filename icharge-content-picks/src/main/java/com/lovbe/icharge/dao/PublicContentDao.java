package com.lovbe.icharge.dao;

import com.lovbe.icharge.common.model.dto.ArticleDo;
import com.lovbe.icharge.common.model.dto.ColumnDo;
import com.lovbe.icharge.common.model.dto.ContentDo;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
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
                    SELECT c.*, ci.uid collect_id  
                    FROM c_article c
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
    List<ArticleDo> selectArticleList(@Param("articleIds") List<Long> articleIds);

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
     * @description &#x83B7;&#x53D6;&#x7528;&#x6237;&#x5217;&#x8868;
     * @param[1] userIds
     * @return List<UserInfoDo>
     * @author lovbe0210
     * @date 2024/12/1 15:25
     */
    List<UserInfoDo> selectUserList(@Param("userIds") Collection<Long> userIds);
}
