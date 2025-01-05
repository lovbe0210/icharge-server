package com.lovbe.icharge.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/31 15:15
 * @Description: ai审核结果
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AIPortraitTagResultDTO {
    private boolean result = true;
    /**
     * 兴趣爱好标签
     */
    private List<String> category;
    /**
     * 内容推荐标签
     */
    private List<String> tags;
}
