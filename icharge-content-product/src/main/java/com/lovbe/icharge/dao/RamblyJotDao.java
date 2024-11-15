package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.entity.dto.ArticleDo;
import com.lovbe.icharge.entity.dto.RamblyJotDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;


/**
 * 随笔相关mapper
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 23:11
 */
@Mapper
public interface RamblyJotDao extends BaseMapper<RamblyJotDo> {

}
