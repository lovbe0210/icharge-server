package com.lovbe.icharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovbe.icharge.common.model.dto.TargetStatisticDo;
import com.lovbe.icharge.entity.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/17 23:57
 * @Description: 社交通知
 */
@Mapper
public interface SocialNoticeDao extends BaseMapper<SocialNoticeDo> {
}
