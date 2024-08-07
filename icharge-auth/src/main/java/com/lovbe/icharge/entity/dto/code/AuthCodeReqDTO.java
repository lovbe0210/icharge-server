package com.lovbe.icharge.entity.dto.code;


import com.lovbe.icharge.enums.CodeSceneEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuthCodeReqDTO {

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
    private CodeSceneEnum scene;
    /**
     * 验证码
     */
    private String code;
    /**
     * 用户ip
     */
    private String usedIp;

}
