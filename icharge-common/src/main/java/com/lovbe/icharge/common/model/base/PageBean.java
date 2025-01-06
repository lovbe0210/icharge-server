package com.lovbe.icharge.common.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/6 22:34
 * @Description: 分页相关参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageBean<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 是否有更多
     */
    private boolean hasMore;
    /**
     * 数据list
     */
    private List<T> list;
}
