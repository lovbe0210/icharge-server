package com.lovbe.icharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import org.apache.ibatis.annotations.Mapper;


/**
 * 用户相关mapper
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 23:11
 */
@Mapper
public interface UserMapper extends BaseMapper<UserInfoDo> {

}
