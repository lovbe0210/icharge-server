package com.lovbe.icharge.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.lovbe.icharge.entity.dto.ColumnDTO;
import com.lovbe.icharge.entity.dto.CreateColumnDTO;
import com.lovbe.icharge.entity.vo.ColumnVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

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

    /**
     * @description 专栏内容编辑
     * @param[1] columnDTO
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/11/3 17:08
     */
    void updateColumn(ColumnDTO columnDTO, long userId);

    /**
     * @description 获取专栏列表
     * @param[1] userId
     * @return List<ColumnVo>
     * @author lovbe0210
     * @date 2024/11/3 18:11
     */
    List<ColumnVo> getColumnList(long userId);

    /**
     * @description 删除专栏
     * @param[1] columnRequest
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/11/5 1:08
     */
    void deleteColumnInfo(ColumnDTO columnDTO, long userId);

    /**
     * @description 获取专栏目录
     * @param[1] columnId
     * @param[2] userId
     * @return JSONObject
     * @author lovbe0210
     * @date 2024/11/7 23:51
     */
    JSONArray getColumnDir(Long columnId, long userId);

    /**
     * @description 专栏目录更新
     * @param[1] columnDTO
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/11/7 23:53
     */
    void updateColumnDir(ColumnDTO columnDTO, long userId);
}
