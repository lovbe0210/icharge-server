package com.lovbe.icharge.common.model.dto;

import com.lovbe.icharge.common.annotation.EsFieldType;
import com.lovbe.icharge.common.annotation.EsIndex;
import com.lovbe.icharge.common.enums.SysConstant;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EsIndex(SysConstant.ES_INDEX_COLUMN)
public class ColumnEsEntity {
    /**
     * 主键 避免超出长度 前端丢失精度
     */
    @EsFieldType(SysConstant.ES_FIELD_TYPE_LONG)
    private Long uid;
    /**
     * 专栏标题
     */
    @EsFieldType(SysConstant.ES_FIELD_TYPE_TEXT)
    private String title;
    /**
     * 专栏简介
     */
    @EsFieldType(SysConstant.ES_FIELD_TYPE_TEXT)
    private String synopsis;
    /**
     * 是否公开
     */
    @EsFieldType(SysConstant.ES_FIELD_TYPE_INTEGER)
    private Integer isPublic;
}
