package com.lovbe.icharge.common.service;

import com.lovbe.icharge.common.enums.EncorageBehaviorEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.dto.*;

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
     * @param: Integer targetType 类型
     * @param: String  textValue
     * @return: AIAuditResultDTO
     * @author: lovbe0210
     * @date: 2025/1/5 17:07
     */
    AIAuditResultDTO sendAuditChat(Integer targetTypeArticle, String textValue);

    /**
     * @description: 用户画像By-kimi
     * @param: String
     * @return: AIAuditResultDTO
     * @author: lovbe0210
     * @date: 2025/1/5 17:07
     */
    AIPortraitTagResultDTO sendPortraitTagChat(String tags);

    /**
     * @description: 更新/删除文章到elasticsearch
     * @param: ArticleEsEntity
     * @return: void
     * @author: lovbe0210
     * @date: 2025/1/5 11:04
     */
    void updateElasticsearchArticle(List<ArticleEsEntity> esArticleList);
    void deleteElasticsearchArticle(List<String> articleIdList);

    /**
     * @description: 更新用户信息到elasticsearch
     * @param: ArticleEsEntity
     * @return: void
     * @author: lovbe0210
     * @date: 2025/1/5 11:04
     */
    void updateElasticsearchUser(List<UserEsEntity> esUserList);

    /**
     * @description: 更新/删除专栏信息到elasticsearch
     * @param: ArticleEsEntity
     * @return: void
     * @author: lovbe0210
     * @date: 2025/1/5 11:04
     */
    void updateElasticsearchColumn(List<ColumnEsEntity> esColumnList);
    void deleteElasticsearchColumn(List<String> columnidList);

    /**
     * @description: 用户经验升级
     * @param: userId
     * @param: i
     * @author: lovbe0210
     * @date: 2025/3/18 22:38
     */
    void updateUserLevel(Long userId, int exp);

    /**
     * @description: 激励电池入库
     * @param: userId
     * @param: uid
     * @param: title
     * @param: encorageBehaviorEnum
     * @author: lovbe0210
     * @date: 2025/3/18 23:35
     */
    void saveEncourageLog(Long userId, Long targetId, String title, EncorageBehaviorEnum encorageBehaviorEnum);
    void saveEncourageLog(List<EncourageLogDo> encourageLogList);
}
