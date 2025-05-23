package com.lovbe.icharge.controller;

import cn.hutool.core.codec.Base64;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.UploadDTO;
import com.lovbe.icharge.common.model.dto.UrlUploadDTO;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.entity.dto.ConversationDTO;
import com.lovbe.icharge.entity.dto.ConversationUpdateDTO;
import com.lovbe.icharge.entity.dto.MessageActionDTO;
import com.lovbe.icharge.entity.vo.MessageActionVo;
import com.lovbe.icharge.entity.vo.MessageSessionVo;
import com.lovbe.icharge.service.ChatMessageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/28 13:20
 * @Description: 聊天消息相关接口
 */
@RestController
public class ChatMessageController {
    private static final Logger log = LoggerFactory.getLogger(ChatMessageController.class);
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
        String jsonString = JsonUtils.toJsonString(sessionList);
        String encodeStr = CommonUtils.bitwiseInvert(Base64.encode(jsonString));
        return ResponseBean.ok(encodeStr);
    }

    /**
     * @description: 发起消息会话
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.entity.vo.MessageSessionVo>
     * @author: lovbe0210
     * @date: 2025/2/28 15:42
     */
    @PostMapping("/chat/session/new")
    public ResponseBean<Long> startMessageSession(@RequestBody @Valid BaseRequest<ConversationDTO> baseRequest,
                                                              @RequestHeader(SysConstant.USERID) Long userId) {
        Long sessionId = chatMessageService.startMessageSession(baseRequest.getData(), userId);
        return ResponseBean.ok(sessionId);
    }

    /**
     * @description: 会话状态更新
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.entity.vo.MessageSessionVo>
     * @author: lovbe0210
     * @date: 2025/2/28 15:42
     */
    @PostMapping("/chat/session/update")
    public ResponseBean<Long> updateMessageSession(@RequestBody @Valid BaseRequest<ConversationUpdateDTO> baseRequest,
                                                   @RequestHeader(SysConstant.USERID) Long userId) {
        chatMessageService.updateMessageSession(baseRequest.getData(), userId);
        return ResponseBean.ok();
    }


    /**
     * @description: 会话删除
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.entity.vo.MessageSessionVo>
     * @author: lovbe0210
     * @date: 2025/2/28 15:42
     */
    @PostMapping("/chat/session/delete")
    public ResponseBean<Long> deleteMessageSession(@RequestBody @Valid BaseRequest<ConversationUpdateDTO> baseRequest,
                                                   @RequestHeader(SysConstant.USERID) Long userId) {
        chatMessageService.deleteMessageSession(baseRequest.getData(), userId);
        return ResponseBean.ok();
    }

    /**
     * @description: 消息删除
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.entity.vo.MessageSessionVo>
     * @author: lovbe0210
     * @date: 2025/2/28 15:42
     */
    @PostMapping("/chat/message/delete")
    public ResponseBean<MessageActionVo> deleteMessageLog(@RequestBody @Valid BaseRequest<MessageActionDTO> baseRequest,
                                               @RequestHeader(SysConstant.USERID) Long userId) {
        MessageActionVo messageActionVo = chatMessageService.deleteMessageLog(baseRequest.getData().getMessageId(), userId);
        return ResponseBean.ok(messageActionVo);
    }

    /**
     * @description: 消息撤回
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.entity.vo.MessageSessionVo>
     * @author: lovbe0210
     * @date: 2025/2/28 15:42
     */
    @PostMapping("/chat/message/rollback")
    public ResponseBean<MessageActionVo> rollbackMessageLog(@RequestBody @Valid BaseRequest<MessageActionDTO> baseRequest,
                                                          @RequestHeader(SysConstant.USERID) Long userId) {
        MessageActionVo messageActionVo = chatMessageService.rollbackMessageLog(baseRequest.getData().getMessageId(), userId);
        return ResponseBean.ok(messageActionVo);
    }

    /**
     * @description: 通过url上传文件
     * @param: uploadDTO
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/3/11 22:25
     */
    @PostMapping("/chat/upload")
    public ResponseBean upload(@Valid UploadDTO uploadDTO,
                               @RequestHeader(value = SysConstant.USERID) Long userId) {
        MultipartFile file = uploadDTO.getFile();
        if (file == null) {
            throw new ServiceException(ServiceErrorCodes.FILE_IS_NULL);
        }
        if (file.getSize() > SysConstant.SIZE_5MB) {
            throw new ServiceException(ServiceErrorCodes.FILE_OUT_SIZE_5);
        }
        String path = chatMessageService.uploadChatFile(uploadDTO, userId);
        return ResponseBean.ok(path);
    }

}
