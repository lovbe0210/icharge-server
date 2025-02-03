package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.entity.dto.BatchUserRequestDTO;
import com.lovbe.icharge.entity.dto.ForgetPasswordDTO;
import com.lovbe.icharge.entity.dto.UpdateUserDTO;
import me.zhyd.oauth.request.AuthRequest;

import java.util.List;

/**
 * @Author: lovbe
 * @Date: 2024/8/13 22:36
 * @Description: 用户相关接口
 */
public interface UserService {
    /**
     * @description: 获取登录用户信息，如果不存在就创建用户
     * @param: AuthUserDTO
     * @return: LoginUser
     * @author: lovbe0210
     * @date: 2024/8/13 22:37
     */
    LoginUser createUserIfAbsent(AuthUserDTO authUserDTO);

    /**
     * @description 根据登录方式获取用户账号信息
     * @param[1] authUserDTO
     * @return LoginUser
     * @author lovbe0210
     * @date 2024/8/14 23:01
     */
    LoginUser getLoginUserByPayload(AuthUserDTO authUserDTO);

    /**
     * @description: 登陆用户获取用户信息
     * @param: Long
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/9/30 16:55
     */
    UserInfoDo getUserInfo(Long userId);
    UserInfoDo getUserInfo(String domain);

    /**
     * @description 更新用户信息
     * @param[1] userId
     * @param[2] userDTO
     * @author lovbe0210
     * @date 2024/10/7 23:30
     */
    void updateUserInfo(Long userId, UpdateUserDTO userDTO);

    /**
     * @description: 通过userId批量获取用户信息
     * @param: BaseRequest<BatchUserRequestDTO>
     * @return: List<UserInfoDo>
     * @author: lovbe0210
     * @date: 2024/12/14 23:48
     */
    List<UserInfoDo> getUserInfoList(BaseRequest<BatchUserRequestDTO> batchRequest);

    /**
     * @description: 获取三方登录地址
     * @return: me.zhyd.oauth.request.AuthRequest
     * @author: lovbe0210
     * @date: 2025/2/3 13:09
     */
    AuthRequest getAuthRequest();

}
