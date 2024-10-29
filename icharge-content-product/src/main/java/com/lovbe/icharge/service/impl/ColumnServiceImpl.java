package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.entity.dto.ArticleDo;
import com.lovbe.icharge.entity.dto.ColumnDo;
import com.lovbe.icharge.entity.dto.CreateColumnDTO;
import com.lovbe.icharge.entity.vo.ColumnVo;
import com.lovbe.icharge.mapper.ColumnMapper;
import com.lovbe.icharge.service.ColumnService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/29 22:44
 * @Description: MS
 */
@Service
public class ColumnServiceImpl implements ColumnService {
    @Resource
    private ColumnMapper columnMapper;

    @Override
    public ColumnVo createColumn(CreateColumnDTO data, long userId) {
        ColumnDo columnDo = new ColumnDo();
        columnDo.setUid(YitIdHelper.nextId());
        columnDo.setTitle(data.getTitle())
                .setSynopsis(data.getSynopsis())
                .setIsPublic(data.getIsPublic())
                .setUserId(userId);
        columnMapper.insert(columnDo);
        ColumnVo columnVo = new ColumnVo();
        BeanUtil.copyProperties(columnDo, columnVo);
        return columnVo;
    }

    @Override
    public ColumnVo getColumnForEdit(Long columnId, long userId) {
        ColumnDo columnDo = columnMapper.selectById(columnId);
        checkColumnStatus(userId, columnDo);
        ColumnVo columnVo = new ColumnVo();
        BeanUtil.copyProperties(columnDo, columnVo);
        return columnVo;
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
