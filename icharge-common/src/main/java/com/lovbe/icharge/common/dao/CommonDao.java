package com.lovbe.icharge.common.dao;

import com.lovbe.icharge.common.model.dto.CacheUserDo;
import com.lovbe.icharge.common.model.dto.EncourageLogDo;
import com.lovbe.icharge.common.model.dto.MenuDTO;
import org.apache.ibatis.annotations.*;

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
    CacheUserDo getUserById(@Param("userId") Long userId);

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

    @Update(value = """
                    UPDATE p_user 
                    SET growth_value = #{userInfo.growthValue},
                        level = #{userInfo.level}
                    WHERE uid = #{userInfo.uid}
                        AND status = 'A'
                    """)
    void updateUserById(@Param("userInfo") CacheUserDo userById);

    @Insert(value = """
                    INSERT INTO p_encorage_log (uid, user_id, behavior_type, target_id, target_name, encourage_score)
                    VALUES(#{encourageLog.uid}, #{encourageLog.userId}, #{encourageLog.behaviorType}, 
                           #{encourageLog.target_id}, #{encourageScore})
                   """)
    void insertEncourageLog(@Param("encourageLog") EncourageLogDo encourageLogDo);
}
