package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.AccountDo;
import org.apache.ibatis.annotations.Mapper;


/**
 * 账号相关mapper
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 23:11
 */
@Mapper
public interface AccountMapper extends BaseMapper<AccountDo> {

}
