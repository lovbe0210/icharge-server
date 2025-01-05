package com.lovbe.icharge.common.model.dto;

import com.lovbe.icharge.common.annotation.EsFieldType;
import com.lovbe.icharge.common.annotation.EsIndex;
import com.lovbe.icharge.common.enums.SysConstant;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EsIndex(SysConstant.ES_INDEX_USER)
public class UserEsEntity {
    /**
     * 主键 避免超出长度 前端丢失精度
     */
    @EsFieldType("long")
    private Long uid;
    /**
     * 文章标题
     */
    @EsFieldType("text")
    private String username;
    /**
     * 兴趣爱好
     */
    @EsFieldType(value = "text", analyzer = "ik_smart")
    private String category;
    /**
     * 推荐系统标签
     */
    @EsFieldType(value = "text", analyzer = "ik_smart")
    private String tags;
}
