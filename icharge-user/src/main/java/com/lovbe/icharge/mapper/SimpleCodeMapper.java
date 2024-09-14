package com.lovbe.icharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.AccountDo;
import com.lovbe.icharge.common.model.dto.SimpleCodeReqDTO;
import com.lovbe.icharge.common.model.dto.VCodeTemplateDO;
import org.apache.ibatis.annotations.Mapper;


/**
 * 账号相关mapper
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 23:11
 */
@Mapper
public interface SimpleCodeMapper extends BaseMapper<VCodeTemplateDO> {

}
