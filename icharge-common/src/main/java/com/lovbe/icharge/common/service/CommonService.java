package com.lovbe.icharge.common.service;

import com.lovbe.icharge.common.model.dto.MenuDTO;
import com.lovbe.icharge.common.model.dto.UserInfoDo;

import java.util.List;


/**
 * @Author: lovbe0210
 * @Date: 2024/12/20 20:11
 * @Description: 公共服务
 */
public interface CommonService {
    /**
     * @description: 获取用户缓存信息
     * @param: Long
     * @return: UserInfoDo
     * @author: lovbe0210
     * @date: 2024/12/20 23:49
     */
    UserInfoDo getCacheUser(Long userId);

    /**
     * @description: 发送消息
     * @param appName
     * @param topic
     * @param t
     * @param <T>
     * @date: 2024/12/20 23:49
     */
    <T> void sendMessage(String appName, String topic, T t);

    /**
     * @description 获取所有菜单
     * @return List<MenuDTO>
     * @author lovbe0210
     * @date 2024/10/24 23:52
     */
    List<MenuDTO> getMenuList();

    /**
     * @description 获取展示用id
     * @return String
     * @author lovbe0210
     * @date 2024/11/17 17:17
     */
    String getBeautifulId();
}
