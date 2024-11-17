package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.entity.dto.MenuDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/24 23:52
 * @Description: MS
 */
@Mapper
public interface CommonDao extends BaseMapper<MenuDTO> {
    @Select(value = """
                    SELECT Distinct uri FROM 
                    (SELECT uri FROM c_article WHERE user_id = #{userId} AND status = 'A'
                    UNION ALL
                    SELECT uri FROM c_column WHERE user_id = #{userId} AND status = 'A') result
                    """)
    List<String> selectUriByUserId(@Param("userId") Long userId);
}
