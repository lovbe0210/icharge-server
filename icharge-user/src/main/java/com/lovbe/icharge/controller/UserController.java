package com.lovbe.icharge.controller;

import cn.hutool.db.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.lovbe.icharge.common.enums.CodeSceneEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.entity.dto.*;
import com.lovbe.icharge.entity.vo.UserStatisticVo;
import com.lovbe.icharge.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @description: 用户信息相关控制层
 * @author: Lvhl
 * @date: 2024/7/9 11:33
 */
@RestController
public class UserController {
    @Resource
    private UserService userService;

    /**
     * description: 通过登录/注册的方式返回用户信息，如果不存在就创建
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param authUserDTO
     * @return ResponseBean<LoginUser>
     */
    @PostMapping("/createIfAbsent")
    public ResponseBean<LoginUser> createUserIfAbsent(@RequestBody @Valid BaseRequest<AuthUserDTO> authUserDTO) {
        return ResponseBean.ok(userService.createUserIfAbsent(authUserDTO.getData()));
    }

    /**
     * description: 通过手机号或邮箱获取用户信息
     * @author: Lvhl
     * @date: 2024/9/16 11:59
     * @param authUserDTO
     * @return ResponseBean<LoginUser>
     */
    @PostMapping("/getUser/loginPayload")
    public ResponseBean<LoginUser> getLoginUserByPayload(@RequestBody @Valid BaseRequest<AuthUserDTO> authUserDTO) {
        return ResponseBean.ok(userService.getLoginUserByPayload(authUserDTO.getData()));
    }

    /**
     * @description: 通过domain获取用户信息
     * @param: String
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/14 23:38
     */
    @GetMapping("/domain/{domain}")
    public ResponseBean getUserInfo(@PathVariable("domain")String domain) {
        return ResponseBean.ok(userService.getUserInfo(domain));
    }

    /**
     * @description: 获取个人主页内容
     * @param: String
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/14 23:38
     */
    @GetMapping("/dmcontent/{contentId}")
    public ResponseBean getDomainContent(@PathVariable("contentId") Long contentId) {
        return ResponseBean.ok(userService.getDomainContent(contentId));
    }

    /**
     * @description: 获取个人主页内容
     * @param: String
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/14 23:38
     */
    @PostMapping("/dmcontent/update")
    public ResponseBean updateDomainContent(@RequestHeader(SysConstant.USERID)Long userId,
                                            @RequestBody @Valid BaseRequest<DomainContentUpdateDTO> contentUpdateRequest) {
        DomainContentUpdateDTO data = contentUpdateRequest.getData();
        Assert.notNull(data.getContent(), "个人主页内容不得为空");
        userService.updateDomainContent(data, userId);
        return ResponseBean.ok();
    }

    /**
     * @description: 获取个人主页内容
     * @param: String
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/14 23:38
     */
    @PostMapping("/dmcontent/delete")
    public ResponseBean deleteDomainContent(@RequestHeader(SysConstant.USERID)Long userId,
                                            @RequestBody @Valid BaseRequest<DomainContentUpdateDTO> contentUpdateRequest) {
        DomainContentUpdateDTO data = contentUpdateRequest.getData();
        Assert.notNull(data.getContentId(), "个人主页内容id不得为空");
        userService.deleteDomainContent(data, userId);
        return ResponseBean.ok();
    }

    /**
     * @description: 通过userId获取用户信息
     * @param: Long
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/14 23:39
     */
    @GetMapping("/id/{userId}")
    public ResponseBean getUserInfo(@PathVariable(SysConstant.USERID)Long userId) {
        return ResponseBean.ok(userService.getUserInfo(userId));
    }

    /**
     * @description: 批量获取用户信息
     * @param: BaseRequest<BatchUserRequestDTO>
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/14 23:39
     */
    @PostMapping("/ids")
    public ResponseBean<PageBean<UserInfoDo>> getUserInfoList(@RequestBody @Valid BaseRequest<Page> batchRequest) {
        return ResponseBean.ok(userService.getUserInfoList(batchRequest.getData()));
    }

    /**
     * @description: 用户信息更新
     * @param: Long
     * @return: ResponseBean
     * @author: lovbe0210
     * @date: 2024/12/14 23:58
     */
    @PutMapping("/update")
    public ResponseBean updateUserInfo(@RequestHeader(name = SysConstant.USERID) Long userId,
                                       @Validated UpdateUserDTO userDTO) {
        MultipartFile avatarFile = userDTO.getAvatarFile();
        if (avatarFile != null && avatarFile.getSize() > SysConstant.SIZE_5MB) {
            throw new ServiceException(ServiceErrorCodes.FILE_OUT_SIZE_5);
        }
        userService.updateUserInfo(userId, userDTO);
        return ResponseBean.ok();
    }

    /**
     * @description: 获取用户社交统计信息
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/3/14 0:23
     */
    @GetMapping("/statistic")
    public ResponseBean getStatisticInfo(@RequestHeader(name = SysConstant.USERID) Long userId) {
        UserStatisticVo statisticVo = userService.getStatisticInfo(userId);
        return ResponseBean.ok(statisticVo);
    }

    /**
     * @description: 获取等级对应经验条
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/3/19 22:36
     */
    @GetMapping("/level/exp")
    public ResponseBean getLevelScore() {
        return ResponseBean.ok(userService.getLevelExp());
    }

    /**
     * @description: 获取每日奖励
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/3/19 22:36
     */
    @GetMapping("/encourage/daily")
    public ResponseBean getEncourageDaily(@RequestHeader(name = SysConstant.USERID) Long userId) {
        return ResponseBean.ok(userService.getEncourageDaily(userId));
    }


    @PostMapping("/oauth/render")
    public void getOAuthRender(@RequestBody BaseRequest<OAuthLoginDTO> baseRequest,
                                       HttpServletResponse response) throws IOException {
        AuthRequest authRequest = userService.getAuthRequest();
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    @RequestMapping("/oauth/callback")
    public Object login(AuthCallback callback) {
        AuthRequest authRequest = userService.getAuthRequest();
        return authRequest.login(callback);
    }
}
