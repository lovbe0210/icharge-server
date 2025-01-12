package com.lovbe.icharge.common.dao;

import com.lovbe.icharge.common.model.dto.CacheUserDo;
import com.lovbe.icharge.common.model.dto.MenuDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/20 22:38
 * @Description: MS
 */
@Mapper
public interface CommonDao {

    @Select(value = """
                    SELECT *, tags tagsStr FROM p_user WHERE uid = #{userId}
                    """)
    CacheUserDo getUserById(Long userId);

    @Select(value = """
                    SELECT Distinct uri FROM 
                    (SELECT uri FROM c_article WHERE status = 'A'
                    UNION ALL
                    SELECT uri FROM c_column WHERE status = 'A') result
                    """)
    List<String> selectAllUri();

    @Select(value = """
                    SELECT * FROM t_menu WHERE status = 'A' ORDER BY type,sort
                    """)
    List<MenuDTO> selectList();
}
