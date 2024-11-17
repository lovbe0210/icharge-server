package com.lovbe.icharge.service;

import com.lovbe.icharge.entity.dto.MenuDTO;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/24 23:50
 * @Description: MS
 */
public interface CommonService {
    /**
     * @description 获取所有菜单
     * @return List<MenuDTO>
     * @author lovbe0210
     * @date 2024/10/24 23:52
     */
    List<MenuDTO> getMenuList();

    /**
     * @description 获取展示用id
     * @param[1] userId
     * @return String
     * @author lovbe0210
     * @date 2024/11/17 17:17
     */
    String getBeautifulId(Long userId);
}
