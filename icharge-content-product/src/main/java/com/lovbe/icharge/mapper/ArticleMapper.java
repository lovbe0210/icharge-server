package com.lovbe.icharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.AccountDo;
import com.lovbe.icharge.entity.dto.ArticleDo;
import org.apache.ibatis.annotations.Mapper;


/**
 * 文档相关mapper
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 23:11
 */
@Mapper
public interface ArticleMapper extends BaseMapper<ArticleDo> {

}
