package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.PreferenceSettingVo;
import com.lovbe.icharge.entity.MusicInfoVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/9 17:12
 * @Description: 偏好设置
 */
@Mapper
public interface MusicPlayDao extends BaseMapper<MusicInfoVo> {
}
