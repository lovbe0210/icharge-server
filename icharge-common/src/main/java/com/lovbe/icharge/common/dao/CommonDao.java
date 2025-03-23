package com.lovbe.icharge.common.dao;

import com.lovbe.icharge.common.model.dto.CacheUserDo;
import com.lovbe.icharge.common.model.dto.EncourageLogDo;
import com.lovbe.icharge.common.model.dto.MenuDTO;
import com.lovbe.icharge.common.model.dto.SocialNoticeDo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/20 22:38
 * @Description: MS
 */
@Mapper
public interface CommonDao {
    /**
     * @description: 查询用户信息
     * @param: userId
     * @return: com.lovbe.icharge.common.model.dto.CacheUserDo
     * @author: lovbe0210
     * @date: 2025/3/19 19:41
     */
    @Select(value = """
            SELECT *, tags tagsStr FROM p_user WHERE uid = #{userId}
            """)
    CacheUserDo getUserById(@Param("userId") Long userId);

    /**
     * @description: 获取所有uri
     * @return: java.util.List<java.lang.String>
     * @author: lovbe0210
     * @date: 2025/3/19 19:40
     */
    @Select(value = """
            SELECT Distinct uri FROM 
            (SELECT uri FROM c_article WHERE status = 'A'
            UNION ALL
            SELECT uri FROM c_column WHERE status = 'A') result
            """)
    List<String> selectAllUri();

    /**
     * @description: 获取菜单列表
     * @return: java.util.List<com.lovbe.icharge.common.model.dto.MenuDTO>
     * @author: lovbe0210
     * @date: 2025/3/19 19:40
     */
    @Select(value = """
            SELECT * FROM t_menu WHERE status = 'A' ORDER BY type,sort
            """)
    List<MenuDTO> selectList();

    /**
     * @description: 用户经验等级升级
     * @param: userById
     * @author: lovbe0210
     * @date: 2025/3/19 19:40
     */
    @Update(value = """
            UPDATE p_user 
            SET growth_value = #{userInfo.growthValue},
                level = #{userInfo.level}
            WHERE uid = #{userInfo.uid}
                AND status = 'A'
            """)
    void updateUserById(@Param("userInfo") CacheUserDo userById);

    /**
     * @description: 单条插入激励明细
     * @param: encourageLogDo
     * @author: lovbe0210
     * @date: 2025/3/19 19:39
     */
    @Insert(value = """
             <script>
                  INSERT INTO p_encorage_log (uid, user_id, behavior_type, target_id, target_name, encourage_score)
                  VALUES
                  <foreach collection="encourageLogList" item="encourageLog" separator=",">
                        (#{encourageLog.uid}, #{encourageLog.userId}, #{encourageLog.behaviorType}, 
                         #{encourageLog.targetId}, #{encourageLog.targetName}, #{encourageLog.encourageScore})
                  </foreach>
             </script>
            """)
    void insertEncourageLog(@Param("encourageLogList") List<EncourageLogDo> encourageLogList);

    @Insert(value = """
                    INSERT INTO s_notices (uid, user_id, notice_type, target_id, action_user_id, notice_content) 
                    VALUES (#{noticeDo.uid}, #{noticeDo.userId}, #{noticeDo.noticeType}, 
                            #{noticeDo.targetId}, 0, #{noticeDo.noticeContent})
                    """)
    void insertAuditNotice(@Param("noticeDo") SocialNoticeDo noticeDo);
}