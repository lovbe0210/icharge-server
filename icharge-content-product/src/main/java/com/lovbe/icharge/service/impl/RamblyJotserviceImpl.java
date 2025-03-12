package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.*;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.dao.ContentDao;
import com.lovbe.icharge.dao.CreateRecordDao;
import com.lovbe.icharge.dao.RamblyJotDao;
import com.lovbe.icharge.entity.dto.ContentPublishDTO;
import com.lovbe.icharge.common.model.dto.CreateRecordDo;
import com.lovbe.icharge.entity.dto.RamblyJotDTO;
import com.lovbe.icharge.common.model.vo.RamblyJotVo;
import com.lovbe.icharge.service.RamblyJotService;
import com.lovbe.icharge.service.feign.IndividuationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/15 13:14
 * @Description: MS
 */
@Slf4j
@Service
public class RamblyJotserviceImpl implements RamblyJotService {
    @Resource
    private RamblyJotDao ramblyJotDao;
    @Resource
    private ContentDao contentDao;
    @Resource
    private CreateRecordDao createRecordDao;
    @Resource
    private CommonService commonService;
    @Resource
    private IndividuationService inService;
    // 文档，专栏，随笔，阅读
    @Value("${spring.kafka.topics.action-essay-publish}")
    private String publishEssayTopic;
    @Value("${spring.application.name}")
    private String appName;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RamblyJotVo createRamblyJot(RamblyJotDTO data, long userId) {
        List<String> previewImg = data.getPreviewImg();
        if (!CollectionUtils.isEmpty(previewImg)) {
            String jsonStr = JSONUtil.toJsonStr(previewImg);
            if (jsonStr.length() > 500) {
                throw new ServiceException(ServiceErrorCodes.FILE_PATH_ERROR);
            }
        }
        RamblyJotDo ramblyJotDo = new RamblyJotDo();
        BeanUtil.copyProperties(data, ramblyJotDo);
        ramblyJotDo.setUid(YitIdHelper.nextId())
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        // 正文内容入库
        ContentDo contentDo = new ContentDo();
        contentDo.setUid(YitIdHelper.nextId())
                .setCreateTime(ramblyJotDo.getCreateTime())
                .setUpdateTime(ramblyJotDo.getUpdateTime())
                .setStatus(CommonStatusEnum.NORMAL.getStatus());
        contentDo.setContent(JSONUtil.toJsonStr(data.getContent()));
        contentDao.insert(contentDo);
        ResponseBean<PreferenceSettingVo> preferenceSetting = inService.getPreferenceSetting(userId);
        if (preferenceSetting != null && preferenceSetting.isResult() && preferenceSetting.getData().getContentDefaultPublic() == 0) {
            ramblyJotDo.setIsPublic(0);
        } else {
            ramblyJotDo.setIsPublic(1);
        }
        ramblyJotDo.setContentId(contentDo.getUid())
                .setUserId(userId)
                .setPublishStatus(1)
                .setTitle("灵感时刻-" + LocalDateTimeUtil.format(LocalDateTime.now(), SysConstant.NORMAL_TIME_FORMAT));
        ramblyJotDao.insertOrUpdate(ramblyJotDo);
        RamblyJotVo ramblyJotVo = new RamblyJotVo();
        BeanUtil.copyProperties(ramblyJotDo, ramblyJotVo);

        // 发送审核消息
        commonService.sendMessage(appName, publishEssayTopic,
                new ContentPublishDTO(ramblyJotDo.getUid(), SysConstant.TARGET_TYPE_ESSAY, contentDo.getUid(), ramblyJotDo.getCreateTime()));
        return ramblyJotVo;
    }

    @Override
    public List<RamblyJotVo> getRamblyJotList(long userId) {
        List<RamblyJotDo> ramblyJotDos = ramblyJotDao.selectList(new LambdaQueryWrapper<RamblyJotDo>()
                .eq(RamblyJotDo::getUserId, userId)
                .eq(RamblyJotDo::getStatus, CommonStatusEnum.NORMAL.getStatus())
                .orderByDesc(RamblyJotDo::getCreateTime));
        if (CollectionUtils.isEmpty(ramblyJotDos)) {
            return List.of();
        }
        return ramblyJotDos.stream()
                .map(ramblyJotDo -> {
                    RamblyJotVo ramblyJotVo = new RamblyJotVo();
                    BeanUtil.copyProperties(ramblyJotDo, ramblyJotVo);
                    return ramblyJotVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRamblyJot(Long ramblyJotId, long userId) {
        RamblyJotDo ramblyJotDo = ramblyJotDao.selectById(ramblyJotId);
        checkRamblyJotStatus(userId, ramblyJotDo);

        Long contentId = ramblyJotDo.getContentId();
        if (contentId != null) {
            ContentDo contentDo = new ContentDo();
            contentDo.setUid(contentId);
            contentDo.setStatus(CommonStatusEnum.DELETE.getStatus());
            contentDao.updateById(contentDo);
        }
        ramblyJotDo.setStatus(CommonStatusEnum.DELETE.getStatus());
        ramblyJotDao.updateById(ramblyJotDo);
    }

    @Override
    public void updateRamblyJot(RamblyJotDTO data, long userId) {
        RamblyJotDo ramblyJotDo = ramblyJotDao.selectById(data.getUid());
        checkRamblyJotStatus(userId, ramblyJotDo);
        ramblyJotDo.setIsPublic(data.getIsPublic());
        ramblyJotDao.updateById(ramblyJotDo);
    }

    @Override
    public void handlerPublishAction(List<ContentPublishDTO> collect) {
        // 随笔不可编辑，因此直接获取content内容
        List<Long> contentIdList = collect.stream()
                .map(ContentPublishDTO::getContentId)
                .collect(Collectors.toList());
        List<ContentDo> contentList = contentDao.selectBatchIds(contentIdList);
        HashMap<Long, ContentDo> contentMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(contentList)) {
            contentMap.putAll(contentList.stream().collect(Collectors.toMap(ContentDo::getUid, Function.identity())));
        }
        for (ContentPublishDTO publishDTO : collect) {
            // 获取内容进行审核
            RamblyJotDo ramblyJotDo = ramblyJotDao.selectById(publishDTO.getTargetId());
            if (ramblyJotDo == null || !CommonStatusEnum.isNormal(ramblyJotDo.getStatus())) {
                continue;
            }
            ContentDo contentDo = contentMap.get(publishDTO.getContentId());
            if (contentDo == null || !StringUtils.hasLength(contentDo.getContent())) {
                continue;
            }
            try {
                // 对内容进行解析，获取纯文本内容
                JSONObject parseObj = JSONUtil.parseObj(contentDo.getContent());
                String textValue = CommonUtils.getContentTextValue(parseObj);
                if (log.isDebugEnabled()) {
                    log.debug("[随笔内容审核] --- textValue: {}", textValue);
                }
                UpdateWrapper<RamblyJotDo> updateWrapper = new UpdateWrapper<RamblyJotDo>()
                        .eq("uid", publishDTO.getTargetId());
                // 发送随笔内容审核请求
                AIAuditResultDTO resultDto = commonService.sendAuditChat(SysConstant.TARGET_TYPE_ESSAY, textValue);
                if (resultDto == null) {
                    log.error("[随笔内容审核] --- 大模型审核结果为空，请在日志中查看详细错误");
                    // TODO 对于审核异常的需要放入死信队列手动审核
                    continue;
                }
                // 结果解析ok
                if (resultDto != null && resultDto.isResult()) {
                    log.info("[随笔内容审核] --- 大模型审核通过");
                    // 根据发布时间contentId更新发布状态
                    updateWrapper.set("publish_status", SysConstant.PUBLISH_SUCCESS);
                    // 发布成功创建记录
                    CreateRecordDo recordDo = new CreateRecordDo(SysConstant.TARGET_TYPE_ESSAY, ramblyJotDo.getUserId());
                    recordDo.setUid(ramblyJotDo.getUid())
                            .setStatus(CommonStatusEnum.NORMAL.getStatus())
                            .setCreateTime(new Date())
                            .setUpdateTime(recordDo.getCreateTime());
                    createRecordDao.insertOrUpdate(recordDo);
                } else {
                    log.info("[随笔内容审核] --- kimi审核失败, reason: {}", resultDto.getReason());
                    updateWrapper.set("publish_status", SysConstant.PUBLISH_FAILED);
                }
                ramblyJotDao.update(updateWrapper);
            } catch (Exception e) {
                log.error("[随笔内容审核] --- 正文内容解析失败，contentId: {}, errorInfo: {}", publishDTO.getContentId(), e.toString());
            }
        }
    }

    private static void checkRamblyJotStatus(long userId, RamblyJotDo ramblyJotDo) {
        if (ramblyJotDo == null || !CommonStatusEnum.isNormal(ramblyJotDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.RAMBLY_JOT_NOT_EXIST);
        }
        if (ramblyJotDo.getUserId() != userId) {
            throw new ServiceException(GlobalErrorCodes.LOCKED);
        }
    }
}