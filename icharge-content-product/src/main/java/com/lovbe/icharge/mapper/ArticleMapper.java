package com.lovbe.icharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.AccountDo;
import com.lovbe.icharge.entity.dto.ArticleDo;
import com.lovbe.icharge.entity.vo.ArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * 文档相关mapper
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 23:11
 */
@Mapper
public interface ArticleMapper extends BaseMapper<ArticleDo> {
    @Select(value = """
            SELECT
            	ca.*,
            	cc.title column_name,
            	ct.content latest_content
            FROM
            	c_article ca
            	LEFT JOIN c_column cc ON ca.column_id = cc.uid
            	LEFT JOIN c_content ct ON ca.latest_content_id = ct.uid
            WHERE
            	ca.uid = #{articleId}
                AND ca.user_id = #{userId}
            """)
    ArticleVO selectArticleForEdit(@Param("userId") long userId, @Param("articleId") String articleId);
}
