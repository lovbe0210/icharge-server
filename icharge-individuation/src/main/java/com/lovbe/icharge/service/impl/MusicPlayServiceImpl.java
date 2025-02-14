package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.dao.MusicPlayDao;
import com.lovbe.icharge.entity.MusicInfoVo;
import com.lovbe.icharge.entity.MusicPlayDTO;
import com.lovbe.icharge.service.MusicPlayService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MusicPlayServiceImpl implements MusicPlayService {
    @Resource
    private MusicPlayDao musicPlayDao;

    @Override
    public List<MusicInfoVo> getMusicPlayList(Long userId) {
        List<MusicInfoVo> list = musicPlayDao.selectList(new LambdaQueryWrapper<MusicInfoVo>()
                .eq(MusicInfoVo::getUserId, userId));
        return CollectionUtils.isEmpty(list) ? List.of() : list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMusicPlayList(BaseRequest<List<MusicPlayDTO>> baseRequest, Long userId) {
        List<MusicPlayDTO> data = baseRequest.getData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        musicPlayDao.delete(new LambdaQueryWrapper<MusicInfoVo>()
                .eq(MusicInfoVo::getUserId, userId));
        List<MusicInfoVo> collect = data.stream()
                .map(music -> {
                    MusicInfoVo musicInfoVo = new MusicInfoVo();
                    BeanUtil.copyProperties(music, musicInfoVo);
                    musicInfoVo.setUid(YitIdHelper.nextId())
                            .setStatus(CommonStatusEnum.NORMAL.getStatus())
                            .setUpdateTime(new Date())
                            .setCreateTime(new Date());
                    musicInfoVo.setUserId(userId);
                    return musicInfoVo;
                })
                .collect(Collectors.toList());
        musicPlayDao.insertOrUpdate(collect);
    }

    @Override
    public void addMusic2PlayList(BaseRequest<MusicPlayDTO> baseRequest, Long userId) {
        MusicPlayDTO data = baseRequest.getData();
        Long count = musicPlayDao.selectCount(new LambdaQueryWrapper<MusicInfoVo>()
                .eq(MusicInfoVo::getUserId, userId)
                .eq(MusicInfoVo::getMusicId, data.getMusicId())
                .eq(MusicInfoVo::getPlatformCode, data.getPlatformCode()));
        if (count != null && count > 0) {
            return;
        }
        MusicInfoVo musicInfoVo = new MusicInfoVo()
                .setUserId(userId);
        BeanUtil.copyProperties(data, musicInfoVo);
        musicInfoVo.setUid(YitIdHelper.nextId())
                .setStatus(CommonStatusEnum.NORMAL.getStatus())
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        musicPlayDao.insertOrUpdate(musicInfoVo);
    }

    @Override
    public void deleteMusicPlayList(BaseRequest<MusicPlayDTO> baseRequest, Long userId) {
        MusicPlayDTO data = baseRequest.getData();
        musicPlayDao.delete(new LambdaQueryWrapper<MusicInfoVo>()
                .eq(MusicInfoVo::getUserId, userId)
                .eq(MusicInfoVo::getMusicId, data.getMusicId())
                .eq(MusicInfoVo::getPlatformCode, data.getPlatformCode()));
    }
}
