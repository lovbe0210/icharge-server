package com.lovbe.icharge.service;

import cn.hutool.db.Page;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.dto.AuthUserDTO;
import com.lovbe.icharge.common.model.dto.ContentPublishDTO;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.common.model.dto.UserInfoDo;
import com.lovbe.icharge.common.model.entity.LoginUser;
import com.lovbe.icharge.entity.dto.DomainContentUpdateDTO;
import com.lovbe.icharge.entity.dto.UpdateUserDTO;
import com.lovbe.icharge.entity.vo.EncourageLogVo;
import com.lovbe.icharge.entity.vo.UserStatisticVo;
import me.zhyd.oauth.request.AuthRequest;

import java.util.List;
import java.util.Map;

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
    PageBean<PageBean<UserInfoDo>> getUserInfoList(Page page);

    /**
     * @description: 获取三方登录地址
     * @return: me.zhyd.oauth.request.AuthRequest
     * @author: lovbe0210
     * @date: 2025/2/3 13:09
     */
    AuthRequest getAuthRequest();

    /**
     * @description: 个人主页内容修改
     * @param: contentUpdateRequest
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/2/5 17:44
     */
    void updateDomainContent(DomainContentUpdateDTO contentUpdateRequest, Long userId);

    /**
     * @description: 获取个人主页内容
     * @param: contentId
     * @return: java.lang.Object
     * @author: lovbe0210
     * @date: 2025/2/5 17:59
     */
    Object getDomainContent(Long contentId, Long userId);

    /**
     * @description: 个人主页内容删除
     * @param: data
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/2/6 14:26
     */
    void deleteDomainContent(DomainContentUpdateDTO data, Long userId);

    /**
     * @description: 获取用户统计信息
     * @param: userId
     * @return: com.lovbe.icharge.entity.vo.UserStatisticVo
     * @author: lovbe0210
     * @date: 2025/3/14 0:24
     */
    UserStatisticVo getStatisticInfo(Long userId);

    /**
     * @description: 获取各等级对应分数
     * @return: java.util.Map<java.lang.String,java.lang.Integer>
     * @author: lovbe0210
     * @date: 2025/3/19 22:33
     */
    Map<String, Integer> getLevelExp();

    /**
     * @description: 获取每日奖励进度
     * @return: java.lang.Object
     * @author: lovbe0210
     * @date: 2025/3/19 23:52
     */
    Object getEncourageDaily(Long userId);

    /**
     * @description: 获取激励规则
     * @return: java.lang.Object
     * @author: lovbe0210
     * @date: 2025/3/20 12:45
     */
    Object getEncourageRule();

    /**
     * @description: 获取电池激励明细
     * @param: data
     * @return: com.lovbe.icharge.common.model.base.PageBean<com.lovbe.icharge.entity.vo.EncourageLogVo>
     * @author: lovbe0210
     * @date: 2025/3/20 15:31
     */
    PageBean<EncourageLogVo> getEncourageLog(RequestListDTO data, Long userId);

    /**
     * @description: 处理个人主页内容修改消息消费
     * @param: collect
     * @author: lovbe0210
     * @date: 2025/3/24 16:54
     */
    void handlerPublishAction(List<ContentPublishDTO> collect);
}
