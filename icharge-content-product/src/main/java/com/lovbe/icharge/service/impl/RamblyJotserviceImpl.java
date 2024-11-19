package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.dao.ContentDao;
import com.lovbe.icharge.dao.RamblyJotDao;
import com.lovbe.icharge.common.model.dto.ContentDo;
import com.lovbe.icharge.entity.dto.RamblyJotDTO;
import com.lovbe.icharge.entity.dto.RamblyJotDo;
import com.lovbe.icharge.entity.vo.RamblyJotVo;
import com.lovbe.icharge.service.RamblyJotService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/15 13:14
 * @Description: MS
 */
@Service
public class RamblyJotserviceImpl implements RamblyJotService {
    @Resource
    private RamblyJotDao ramblyJotDao;
    @Resource
    private ContentDao contentDao;

    @Override
    public RamblyJotVo getRamblyJotInfo(long userId, Long ramblyJotId) {
        RamblyJotDo ramblyJotDo = ramblyJotDao.selectById(ramblyJotId);
        if (ramblyJotDo == null || CommonStatusEnum.isDelete(ramblyJotDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.RAMBLY_JOT_NOT_EXIST);
        }
        RamblyJotVo ramblyJotVo = new RamblyJotVo();
        BeanUtil.copyProperties(ramblyJotDo, ramblyJotVo);
        if (ramblyJotDo.getContentId() != null) {
            ContentDo contentDo = contentDao.selectById(ramblyJotDo.getContentId());
            if (contentDo != null) {
                ramblyJotVo.setContent(contentDo.getContent());
            }
        }
        return ramblyJotVo;
    }

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
        contentDo.setContent(data.getContent());
        contentDao.insert(contentDo);
        ramblyJotDo.setContentId(contentDo.getUid())
                .setUserId(userId)
                .setTitle("灵感时刻-" + LocalDateTimeUtil.format(LocalDateTime.now(), SysConstant.NORMAL_TIME_FORMAT));
        ramblyJotDao.insertOrUpdate(ramblyJotDo);
        RamblyJotVo ramblyJotVo = new RamblyJotVo();
        BeanUtil.copyProperties(ramblyJotDo, ramblyJotVo);
        return ramblyJotVo;
    }

    @Override
    public List<RamblyJotVo> getRamblyJotList(long userId) {
        List<RamblyJotDo> ramblyJotDos = ramblyJotDao.selectList(new LambdaQueryWrapper<RamblyJotDo>()
                .eq(RamblyJotDo::getUserId, userId)
                .eq(RamblyJotDo::getStatus, CommonStatusEnum.NORMAL.getStatus())
                .orderByDesc(RamblyJotDo::getUpdateTime));
        if (CollectionUtils.isEmpty(ramblyJotDos)) {
            return List.of();
        }
        return ramblyJotDos.stream().map(ramblyJotDo -> {
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
        if (contentId != null){
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

    private static void checkRamblyJotStatus(long userId, RamblyJotDo ramblyJotDo) {
        if (ramblyJotDo == null || !CommonStatusEnum.isNormal(ramblyJotDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.RAMBLY_JOT_NOT_EXIST);
        }
        if (ramblyJotDo.getUserId() != userId) {
            throw new ServiceException(GlobalErrorCodes.LOCKED);
        }
    }
}
