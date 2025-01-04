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
public class AIAuditResultDTO {
    /**
     * 审核结果
     */
    private boolean result;
    /**
     * 错误原因
     */
    private List<String> reason;
}
