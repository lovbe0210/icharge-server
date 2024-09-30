package com.lovbe.icharge.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "p_user")
public class UserInfoDo extends BaseEntity {
    private String username;
    private String level;
    private String domain;
    private String avatarUrl;
    private String tags;
    private String introduction;
    private String location;
    private String industry;
    private String contentId;
    private String growthValue;
}
