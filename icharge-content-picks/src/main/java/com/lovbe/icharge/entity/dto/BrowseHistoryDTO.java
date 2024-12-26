package com.lovbe.icharge.entity.dto;

import com.lovbe.icharge.common.model.dto.UserInfoDo;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/25 20:26
 * @Description: MS
 */
@Data
@Accessors(chain = true)
public class BrowseHistoryDTO extends BrowseHistoryDo {
    /**
     * 标题
     */
    private String title;
    /**
     * 预览内容
     */
    private String previewContent;
    /**
     * 专栏，文章通过uri来访问
     */
    private String uri;
    /**
     * 文章所属专栏
     */
    private String columnUri;
    /**
     *作者用户名
     */
    private UserInfoDo userInfo;
    /**
     * 浏览次数
     */
    private int viewCount;
    /**
     * 点赞个数
     */
    private int likeCount;
    /**
     * 收藏次数
     */
    private int collectCount;
    /**
     * 评论个数
     */
    private int commentCount;
}
