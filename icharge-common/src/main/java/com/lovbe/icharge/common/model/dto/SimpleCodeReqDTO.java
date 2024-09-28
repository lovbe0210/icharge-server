package com.lovbe.icharge.common.model.dto;


import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.common.validation.Mobile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCodeReqDTO {

    /**
     * 手机号
     */
    @Mobile
    private String mobile;
    /**
     * 邮箱
     */
    @Email
    private String email;
    /**
     * 使用场景
     */
    @NotNull(message = "使用场景不能为空")
    private Integer scene;
    /**
     * 滑块验证码签名
     */
    private String sign;
    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Length(min = 6, max = 6, message = "验证码长度为6位")
    @Pattern(regexp = "^[0-9]+$", message = "验证码必须都是数字")
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
