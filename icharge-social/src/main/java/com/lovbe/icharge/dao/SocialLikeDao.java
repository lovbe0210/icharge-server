package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.entity.dto.LikeActionDo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/8 22:48
 * @Description: 社交点赞
 */
@Mapper
public interface SocialLikeDao extends BaseMapper<LikeActionDo> {
}
