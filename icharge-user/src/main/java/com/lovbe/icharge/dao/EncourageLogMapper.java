package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.EncourageLogDo;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.entity.dto.DomainContentUpdateDTO;
import org.apache.ibatis.annotations.*;


/**
 * 用户相关mapper
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 23:11
 */
@Mapper
public interface EncourageLogMapper extends BaseMapper<EncourageLogDo> {
}
