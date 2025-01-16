package com.lovbe.icharge.common.model.dto;

import com.lovbe.icharge.common.annotation.EsFieldType;
import com.lovbe.icharge.common.annotation.EsIndex;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.util.ElasticSearchUtils;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EsIndex(SysConstant.ES_INDEX_USER)
public class UserEsEntity {
    /**
     * 主键 避免超出长度 前端丢失精度
     */
    @EsFieldType(SysConstant.ES_FIELD_TYPE_LONG)
    private Long uid;
    /**
     * 用户名
     */
    @EsFieldType(value = SysConstant.ES_FIELD_TYPE_TEXT, analyzer = ElasticSearchUtils.STANDARD, searchAnalyzer = ElasticSearchUtils.STANDARD)
    private String username;
    /**
     * 兴趣爱好
     */
    @EsFieldType(value = SysConstant.ES_FIELD_TYPE_TEXT, analyzer = ElasticSearchUtils.IK_SMART)
    private String category;
    /**
     * 推荐系统标签
     */
    @EsFieldType(value = SysConstant.ES_FIELD_TYPE_TEXT, analyzer = ElasticSearchUtils.IK_SMART)
    private String tags;
}
