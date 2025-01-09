package com.lovbe.icharge.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/17 23:59
 * @Description: MS
 */
@Data
public class ReplyCommentDTO extends BaseEntity {
    /**
     * 评论对象id
     */
    @NotNull(message = "评论对象不得为空")
    private Long targetId;
    /**
     * 评论对象类型 1文章 2专栏 3随笔 4评论
     */
    @NotNull(message = "评论对象类型不得为空")
    private Integer targetType;
    /**
     * 父级评论id
     */
    private Long parentId;
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不得为空")
    @Length(max = 1000, message = "评论字数太多，请删减至1000字或以下")
    private String content;
    /**
     * 评论图片
     */
    private MultipartFile contentImgFile;
    /**
     * 楼中楼回复@用户id
     */
    private Long replyUserId;
    /**
     * 评论提交ip
     */
    private String submitIp;
    /**
     * 评论提交城市
     */
    private String submitCity;
}
