package com.lovbe.icharge.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/14 0:22
 * @Description: MS
 */
@Data
@Accessors(chain = true)
public class UserStatisticVo {
    /**
     * 文章数
     */
    private int articleCount;
    /**
     * 专栏数
     */
    private int columnCount;
    /**
     * 用户粉丝数
     */
    private int fansCount;
    /**
     * 用户关注数
     */
    private int followCount;
}
