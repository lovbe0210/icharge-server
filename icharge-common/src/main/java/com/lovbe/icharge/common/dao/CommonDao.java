package com.lovbe.icharge.common.dao;

import com.lovbe.icharge.common.model.dto.UserInfoDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/20 22:38
 * @Description: MS
 */
@Mapper
public interface CommonDao {

    @Select(value = """
                    SELECT * FROM p_user WHERE uid = #{userId}
                    """)
    UserInfoDo getUserById(Long userId);
}
