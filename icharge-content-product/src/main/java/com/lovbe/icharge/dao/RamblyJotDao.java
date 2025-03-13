package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.RamblyJotDo;
import com.lovbe.icharge.common.model.dto.SocialNoticeDo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * 随笔相关mapper
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 23:11
 */
@Mapper
public interface RamblyJotDao extends BaseMapper<RamblyJotDo> {
    @Insert(value = """
                    INSERT INTO s_notices (uid, user_id, notice_type, target_id, action_user_id, notice_content) 
                    VALUES (#{noticeDo.uid}, #{noticeDo.userId}, #{noticeDo.noticeType}, 
                            #{noticeDo.targetId}, 0, #{noticeDo.noticeContent})
                    """)
    void insertAuditNotice(@Param("noticeDo") SocialNoticeDo noticeDo);
}
