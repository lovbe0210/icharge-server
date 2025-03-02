package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.ConversationDTO;
import com.lovbe.icharge.entity.vo.MessageSessionVo;
import com.lovbe.icharge.service.ChatMessageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/28 13:20
 * @Description: 聊天消息相关接口
 */
@RestController
public class ChatMessageController {
    @Resource
    private ChatMessageService chatMessageService;

    /**
     * @description: 获取消息会话列表
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.entity.vo.MessageSessionVo>
     * @author: lovbe0210
     * @date: 2025/2/28 15:42
     */
    @GetMapping("/chat/session/list")
    public ResponseBean<MessageSessionVo> getSessionList(@RequestHeader(SysConstant.USERID) Long userId) {
        List<MessageSessionVo> sessionList = chatMessageService.getSessionList(userId);
        return ResponseBean.ok(sessionList);
    }

    /**
     * @description: 发起消息会话
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.entity.vo.MessageSessionVo>
     * @author: lovbe0210
     * @date: 2025/2/28 15:42
     */
    @PostMapping("/chat/session/new")
    public ResponseBean<Long> startMessageSession(@RequestBody BaseRequest<ConversationDTO> baseRequest,
                                                              @RequestHeader(SysConstant.USERID) Long userId) {
        Long sessionId = chatMessageService.startMessageSession(baseRequest.getData(), userId);
        return ResponseBean.ok(sessionId);
    }
}
