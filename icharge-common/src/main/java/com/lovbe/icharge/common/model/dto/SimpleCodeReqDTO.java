package com.lovbe.icharge.common.model.dto;


import com.lovbe.icharge.common.enums.CodeSceneEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCodeReqDTO {

    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 使用场景
     */
    private Integer scene;
    /**
     * 滑块验证码签名
     */
    private String sign;
    /**
     * 验证码
     */
    private String code;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户ip
     */
    private String usedIp;
}
