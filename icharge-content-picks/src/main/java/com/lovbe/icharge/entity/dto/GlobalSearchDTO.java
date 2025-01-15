package com.lovbe.icharge.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/15 17:19
 * @Description: 全局搜索请求实体
 */
@Data
public class GlobalSearchDTO {
    /**
     * 搜索关键字
     */
    @NotBlank(message = "搜索关键字不得为空")
    @Length(max = 250, message = "关键字太长，请重新编辑后再进行搜索")
    private String keywords;
    /**
     * 搜索范围-用户下所有公开内容
     */
    private Long userId;
    /**
     * 搜索范围-专栏下所有公开内容
     */
    private Long columnId;
    /**
     * 单次请求数据
     */
    private int limit = 20;
    /**
     * 请求坐标
     */
    private int offset = 0;
    /**
     * 排序方式 value: 0 最多浏览 1 最多收藏 2 最近更新
     */
    private String sortType;

    public int getLimit() {
        return limit < 1 ? 20 : limit > 100 ? 20 : limit;
    }

    public int getOffset() {
        return offset < 0 ? 0 : offset;
    }
}
