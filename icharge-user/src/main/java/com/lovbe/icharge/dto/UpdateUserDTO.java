package com.lovbe.icharge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/7 23:16
 * @Description: 用户信息更新实体
 */
@Data
public class UpdateUserDTO {
    /**
     * 头像源文件
     */
    private MultipartFile avatarFile;
    /**
     * 用户名
     */
    @NotBlank(message = "昵称不得为空")
    private String username;
    /**
     * 标签
     */
    private String tagArray;
    /**
     * 简介
     */
    @Length(max = 200, message = "简介字数不得超过200")
    private String introduction;
    /**
     * 位置
     */
    @Length(max = 50, message = "地址字数不得超过200")
    private String location;
    /**
     * 行业，领域
     */
    @Length(max = 50, message = "领域字数不得超过50")
    private String industry;
}
