package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.FileUploadDTO;
import com.lovbe.icharge.dao.ColumnDao;
import com.lovbe.icharge.entity.dto.ArticleDo;
import com.lovbe.icharge.entity.dto.ColumnDTO;
import com.lovbe.icharge.entity.dto.ColumnDo;
import com.lovbe.icharge.entity.dto.CreateColumnDTO;
import com.lovbe.icharge.entity.vo.ColumnVo;
import com.lovbe.icharge.service.ColumnService;
import com.lovbe.icharge.service.feign.StorageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/29 22:44
 * @Description: MS
 */
@Slf4j
@Service
public class ColumnServiceImpl implements ColumnService {
    @Resource
    private ColumnDao columnDao;
    @Resource
    private StorageService storageService;

    @Override
    public ColumnVo createColumn(CreateColumnDTO data, long userId) {
        ColumnDo columnDo = new ColumnDo();
        columnDo.setUid(YitIdHelper.nextId());
        columnDo.setTitle(data.getTitle())
                .setSynopsis(data.getSynopsis())
                .setIsPublic(data.getIsPublic())
                .setUserId(userId);
        columnDao.insert(columnDo);
        ColumnVo columnVo = new ColumnVo();
        BeanUtil.copyProperties(columnDo, columnVo);
        return columnVo;
    }

    @Override
    public ColumnVo getColumnForEdit(Long columnId, long userId) {
        ColumnDo columnDo = columnDao.selectById(columnId);
        checkColumnStatus(userId, columnDo);
        ColumnVo columnVo = new ColumnVo();
        BeanUtil.copyProperties(columnDo, columnVo);
        return columnVo;
    }

    @Override
    public void updateColumn(ColumnDTO columnDTO, long userId) {
        ColumnDo columnDo = columnDao.selectById(columnDTO.getUid());
        checkColumnStatus(userId, columnDo);
        columnDo.setUpdateTime(new Date());
        BeanUtil.copyProperties(columnDTO, columnDo);
        // 判断是否需要更新封面文件
        if (columnDTO.getCoverFile() != null) {
            // 上传文件
            ResponseBean<String> upload = storageService
                    .upload(new FileUploadDTO(columnDTO.getCoverFile(), SysConstant.FILE_SCENE_COVER));
            if (!upload.isResult()) {
                log.error("[更新专栏信息] --- 封面上传失败，errorInfo: {}", upload.getMessage());
                throw new ServiceException(ServiceErrorCodes.ARTICLE_INFO_UPDATE_FAILED);
            }
            columnDo.setCoverUrl(upload.getData());
        }
        columnDao.updateById(columnDo);
    }

    @Override
    public List<ColumnVo> getColumnList(long userId) {
        List<ColumnDo> columnList = columnDao.selectList(userId);
        if (CollectionUtils.isEmpty(columnList)) {
            return ListUtil.empty();
        }
        return columnList.stream().map(column -> {
            List<ArticleDo> articleList = column.getArticleList();
            if (CollectionUtils.isEmpty(articleList)) {
                column.setDirContentId(null);
            }
            ColumnVo columnVo = new ColumnVo();
            BeanUtil.copyProperties(column, columnVo);
            return columnVo;
        }).collect(Collectors.toList());
    }

    private static void checkColumnStatus(long userId, ColumnDo columnDo) {
        if (columnDo == null || !CommonStatusEnum.isNormal(columnDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.COLUMN_NOT_EXIST);
        }
        if (columnDo.getUserId() != userId) {
            throw new ServiceException(GlobalErrorCodes.LOCKED);
        }
    }
}
