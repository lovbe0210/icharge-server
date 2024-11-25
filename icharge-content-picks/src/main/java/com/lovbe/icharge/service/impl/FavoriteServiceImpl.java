package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.dao.FavoriteDao;
import com.lovbe.icharge.dao.FavoriteTagDao;
import com.lovbe.icharge.entity.dto.FavoriteDo;
import com.lovbe.icharge.entity.dto.FavoriteTagsDTO;
import com.lovbe.icharge.entity.dto.FavoriteTargetDTO;
import com.lovbe.icharge.service.FavoriteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/25 23:19
 * @Description: 收藏夹
 */
@Slf4j
@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Resource
    private FavoriteDao favoriteDao;
    @Resource
    private FavoriteTagDao favoriteTagDao;
    @Override
    public void marksContent(FavoriteTargetDTO data, Long userId) {
        FavoriteDo favoriteDb = favoriteDao.selectOne(new LambdaQueryWrapper<FavoriteDo>()
                .eq(FavoriteDo::getUserId, userId)
                .eq(FavoriteDo::getTargetId, data.getTargetId()));
        if (favoriteDb != null) {
            // 更新
            favoriteDb.setTags(data.getTags()).setUpdateTime(new Date());
            favoriteDao.insertOrUpdate(favoriteDb);
            return;
        }
        if (data.getFtId() == null) {
            data.setFtId(YitIdHelper.nextId());
        }
        FavoriteDo favoriteDo = new FavoriteDo();
        BeanUtil.copyProperties(data, favoriteDo);
        favoriteDo.setUid(data.getFtId())
                .setStatus(CommonStatusEnum.NORMAL.getStatus())
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        favoriteDo.setUserId(userId);
        favoriteDao.insertOrUpdate(favoriteDo);
    }

    @Override
    public void cancelMarkContent(Long ftId, Long userId) {
        int delete = favoriteDao.delete(new LambdaQueryWrapper<FavoriteDo>()
                .eq(FavoriteDo::getUid, ftId)
                .eq(FavoriteDo::getUserId, userId));
        if (delete < 1) {
            log.error("[收藏夹] --- 取消收藏失败，数据删除为ftId：{}，userId：{}", ftId, userId);
        }
    }

    @Override
    public List<FavoriteTagsDTO> getFavoriteTagList(Long userId) {
        List<FavoriteTagsDTO> favoriteTagList = favoriteTagDao.selectList(new LambdaQueryWrapper<FavoriteTagsDTO>()
                .eq(FavoriteTagsDTO::getUserId, userId)
                .orderByDesc(FavoriteTagsDTO::getUpdateTime));
        return favoriteTagList;
    }

    @Override
    public void updateFavoriteTag(FavoriteTagsDTO data, Long userId) {
        List<FavoriteTagsDTO> favoriteTagList = favoriteTagDao.selectList(
                new LambdaQueryWrapper<FavoriteTagsDTO>().eq(FavoriteTagsDTO::getUserId, userId));
        if (!CollectionUtils.isEmpty(favoriteTagList)) {
            favoriteTagList.forEach(tag -> {
                if (Objects.equals(tag.getTitle(), data.getTitle())) {
                    throw new ServiceException(ServiceErrorCodes.REPEAT_FAVORITE_TAG);
                }
            });
        }
        data.setUserId(userId);
        if (data.getUid() == null) {
            data.setUid(YitIdHelper.nextId())
                    .setStatus(CommonStatusEnum.NORMAL.getStatus())
                    .setCreateTime(new Date())
                    .setUpdateTime(new Date());
        } else {
            data.setUpdateTime(new Date());
        }
        favoriteTagDao.insertOrUpdate(data);
    }

    @Override
    public void deleteFavoriteTag(FavoriteTagsDTO data, Long userId) {
        int delete = favoriteTagDao.delete(new LambdaQueryWrapper<FavoriteTagsDTO>()
                .eq(FavoriteTagsDTO::getUid, data.getUid())
                .eq(FavoriteTagsDTO::getUserId, userId));
        if (delete < 1) {
            log.error("[收藏夹] --- 删除收藏分组失败，数据删除为tagId：{}，userId：{}", data.getUid(), userId);
        }
    }
}
