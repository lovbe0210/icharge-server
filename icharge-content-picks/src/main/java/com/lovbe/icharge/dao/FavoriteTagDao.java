package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.entity.dto.FavoriteTagsDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/26 0:34
 * @Description: MS
 */
@Mapper
public interface FavoriteTagDao extends BaseMapper<FavoriteTagsDTO> {
}
