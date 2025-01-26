package com.lovbe.icharge.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/31 14:57
 * @Description: 微调参数
 */
@Data
@ConfigurationProperties(prefix = "spring.ai.fine-tune")
public class AIPromptProperties {
    /**
     * 文章审核系统引导语句
     */
    private String articleAuditPromptContent;
    /**
     * 随笔审核系统引导语句
     */
    private String essayAuditPromptContent;
    /**
     * 人物画像系统引导语句
     */
    private String portraitTagPromptContent;
    /**
     * 允许存在词汇白名单
     */
    private String permittedKeywords;
}
