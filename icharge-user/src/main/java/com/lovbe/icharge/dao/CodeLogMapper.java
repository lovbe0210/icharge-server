package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.entity.dto.CodeLogDo;
import org.apache.ibatis.annotations.*;


/**
 * 验证码发送记录
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 23:11
 */
@Mapper
public interface CodeLogMapper extends BaseMapper<CodeLogDo> {
}
