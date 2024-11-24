package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.ColumnDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 专栏相关mapper
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 23:11
 */
@Mapper
public interface ColumnDao extends BaseMapper<ColumnDo> {

    /**
     * @description 获取专栏列表
     * @param[1] userId
     * @return List<ColumnDo>
     * @author lovbe0210
     * @date 2024/11/3 22:59
     */
    List<ColumnDo> selectList(@Param("userId") long userId);
}
