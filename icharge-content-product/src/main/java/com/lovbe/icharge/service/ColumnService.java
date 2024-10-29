package com.lovbe.icharge.service;

import com.lovbe.icharge.entity.dto.CreateColumnDTO;
import com.lovbe.icharge.entity.vo.ColumnVo;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/29 22:33
 * @Description: 专栏
 */
public interface ColumnService {
    /**
     * @description 新建专栏
     * @param[1] data
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/10/29 22:36
     */
    ColumnVo createColumn(CreateColumnDTO data, long userId);

    /**
     * @description 获取专栏信息
     * @param[1] columnId
     * @param[2] userId
     * @return ColumnVo
     * @author lovbe0210
     * @date 2024/10/29 23:26
     */
    ColumnVo getColumnForEdit(Long columnId, long userId);
}
