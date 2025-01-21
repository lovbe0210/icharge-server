package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.common.model.dto.UserInfoDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/17 0:08
 * @Description: 优秀作者
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExcellentAuthorVo extends UserInfoDo {
    /**
     * 点赞数
     */
    private int likeCount;
    /**
     * 收藏数
     */
    private int collectCount;
    /**
     * 浏览量
     */
    private int viewCount;
    /**
     * 文章数
     */
    private int articleCount;
    /**
     * 是否关注
     */
    private int isFollow;
    /**
     * 粉丝数
     */
    private int fansCount;
}
