package com.lovbe.icharge.common.model.entity;

import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserInfo extends BaseEntity {
    private String username;
    private String level;
    private String domain;
    private String avatarUrl;
    private Map tags;
    private String introduction;
    private String location;
    private String industry;
    private String contentId;
    private String growthValue;
}
