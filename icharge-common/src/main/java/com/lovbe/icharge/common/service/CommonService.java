package com.lovbe.icharge.common.service;

import com.lovbe.icharge.common.model.dto.*;

import java.io.IOException;
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

    /**
     * @description: 文章内容审核By-kimi
     * @param: String
     * @return: AIAuditResultDTO
     * @author: lovbe0210
     * @date: 2025/1/5 17:07
     */
    AIAuditResultDTO sendAuditChat(String textValue);

    /**
     * @description: 用户画像By-kimi
     * @param: String
     * @return: AIAuditResultDTO
     * @author: lovbe0210
     * @date: 2025/1/5 17:07
     */
    AIPortraitTagResultDTO sendPortraitTagChat(String tags);

    /**
     * @description: 更新文章到elasticsearch
     * @param: ArticleEsEntity
     * @return: void
     * @author: lovbe0210
     * @date: 2025/1/5 11:04
     */
    void updateElasticsearchArticle(ArticleEsEntity articleEsEntity) throws IOException;

    /**
     * @description: 更新用户信息到elasticsearch
     * @param: ArticleEsEntity
     * @return: void
     * @author: lovbe0210
     * @date: 2025/1/5 11:04
     */
    void updateElasticsearchUser(UserEsEntity userEsEntity) throws IOException;

    /**
     * @description: 更新专栏信息到elasticsearch
     * @param: ArticleEsEntity
     * @return: void
     * @author: lovbe0210
     * @date: 2025/1/5 11:04
     */
    void updateElasticsearchColumn(ColumnEsEntity esEntity) throws IOException;

    /**
     * @description: 删除专栏信息到elasticsearch
     * @param: ArticleEsEntity
     * @return: void
     * @author: lovbe0210
     * @date: 2025/1/5 11:04
     */
    void deleteElasticsearchColumn(ColumnEsEntity columnEsEntity) throws IOException;
}
