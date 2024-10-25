package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.FileUploadDTO;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.entity.dto.*;
import com.lovbe.icharge.entity.vo.ArticleVO;
import com.lovbe.icharge.entity.vo.ContentVO;
import com.lovbe.icharge.mapper.ArticleMapper;
import com.lovbe.icharge.mapper.ColumnMapper;
import com.lovbe.icharge.mapper.ContentMapper;
import com.lovbe.icharge.service.ArticleService;
import com.lovbe.icharge.service.feign.StorageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/9 23:54
 * @Description: MS
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ContentMapper contentMapper;
    @Resource
    private StorageService storageService;
    @Autowired
    private ColumnMapper columnMapper;

    @Override
    public ArticleVO createBlankDoc(long userId) {
        ArticleDo articleDo = new ArticleDo();
        articleDo.setUid(YitIdHelper.nextId())
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        articleDo.setUserId(userId).setTitle("无标题文档");
        articleMapper.insertOrUpdate(articleDo);
        ArticleVO articleVO = new ArticleVO();
        BeanUtil.copyProperties(articleDo, articleVO);
        return articleVO;
    }

    @Override
    public ArticleVO getArticleForEdit(String articleId, long userId) {
        ArticleDo articleDo = articleMapper.selectOne(new LambdaQueryWrapper<ArticleDo>()
                .eq(ArticleDo::getUid, articleId)
                .eq(ArticleDo::getUserId, userId));
        if (articleDo == null || !CommonStatusEnum.isNormal(articleDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }
        if (CommonStatusEnum.isDisable(articleDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_STATUS_ERROR);
        }
        ArticleVO articleVO = new ArticleVO();
        BeanUtil.copyProperties(articleDo, articleVO);
        if (articleDo.getColumnId() == null) {
            return articleVO;
        }
        ColumnDo columnDo = columnMapper.selectById(articleDo.getColumnId());
        if (columnDo == null || !CommonStatusEnum.isNormal(columnDo.getStatus())) {
            articleVO.setColumnId(null);
        }else {
            articleVO.setColumnName(columnDo.getTitle());
        }
        return articleVO;
    }

    @Override
    public void updateArticle(boolean simpleUpdate, ArticleDTO articleDTO, long userId) {
        ArticleDo articleDo = articleMapper.selectById(articleDTO.getUid());
        checkArticleStatus(userId, articleDo);
        String tags = articleDTO.getTagsArray();
        if (StringUtils.hasLength(tags)) {
            articleDo.setTags(JSONUtil.toList(tags, java.util.Map.class));
        }
        articleDo.setUpdateTime(new Date());
        BeanUtil.copyProperties(articleDTO, articleDo);
        // 判断是否需要更新封面文件
        if (!simpleUpdate && articleDTO.getCoverFile() != null) {
            // 上传文件
            ResponseBean<String> upload = storageService
                    .upload(new FileUploadDTO(articleDTO.getCoverFile(), SysConstant.FILE_SCENE_COVER));
            if (!upload.isResult()) {
                log.error("[更新文章信息] --- 封面上传失败，errorInfo: {}", upload.getMessage());
                throw new ServiceException(ServiceErrorCodes.ARTICLE_INFO_UPDATE_FAILED);
            }
            articleDo.setCoverUrl(upload.getData());
        }
        articleMapper.updateById(articleDo);
    }

    @Override
    public Map updateContent(BaseRequest<ContentDTO> contentEntity, long userId) {
        ContentDTO contentDTO = contentEntity.getData();
        ArticleDo articleDo = articleMapper.selectById(contentDTO.getArticleId());
        checkArticleStatus(userId, articleDo);
        ContentDo contentDo = new ContentDo();
        Long uid = contentDTO.getUid();
        Date updateTime = new Date();
        HashMap<String, Object> map = MapUtil.of(SysConstant.UPDATE_TIME, updateTime);
        if (uid == null) {
            uid = YitIdHelper.nextId();
            contentDo.setCreateTime(updateTime);
        }
        contentDo.setUid(uid).setUpdateTime(updateTime);
        contentDo.setContent(JSONUtil.toJsonStr(contentDTO.getContent()));
        contentMapper.insertOrUpdate(contentDo);

        // 判断是否需要更新文档信息
        if (articleDo.getWordsNum() == 0 && contentDTO.getWordsNum() == 0) {
            map.put(SysConstant.CONTENT_ID, uid);
            return map;
        }
        // 是否需要更新摘要
        if (articleDo.getAutoSummary() == 1) {
            articleDo.setSummary(contentDTO.getSummary());
        }
        // 是否需要更新封面
        if (!StringUtils.hasLength(articleDo.getCoverUrl())) {
            articleDo.setCoverUrl(contentDTO.getCoverUrl());
        }

        articleDo.setUpdateTime(updateTime);
        articleDo.setLatestContentId(uid)
                .setWordsNum(contentDTO.getWordsNum())
                .setPublishStatus(0);
        articleMapper.updateById(articleDo);
        map.put(SysConstant.CONTENT_ID, uid);
        return map;
    }

    @Override
    public List<ArticleVO> getMyArticleList(BaseRequest<RequestListDTO> requestDto, long userId) {
        RequestListDTO data = requestDto.getData();
        LambdaQueryWrapper<ArticleDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleDo::getUserId, userId)
                .eq(ArticleDo::getStatus, CommonStatusEnum.NORMAL.getStatus())
                .like(data != null && StringUtils.hasLength(data.getKeywords()), ArticleDo::getTitle, data.getKeywords());
        if (data != null && data.getSort() != null) {
            queryWrapper.orderByDesc(data.getSort() == 1, ArticleDo::getUpdateTime);
            queryWrapper.orderByDesc(data.getSort() == 2, ArticleDo::getCreateTime);
        } else {
            queryWrapper.orderByAsc(ArticleDo::getUid);
        }
        List<ArticleDo> selectList = articleMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(selectList)) {
            return selectList.stream().map(articleDo -> {
                ArticleVO articleVO = new ArticleVO();
                BeanUtil.copyProperties(articleDo, articleVO);
                return articleVO;
            }).collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public ContentVO getContent(Long articleId, long userId) {
        ArticleDo articleDo = articleMapper.selectById(articleId);
        checkArticleStatus(userId, articleDo);
        ContentDo contentDo = contentMapper.selectById(articleDo.getLatestContentId());
        if (contentDo == null) {
            return null;
        }
        ContentVO contentVO = new ContentVO(articleDo.getLatestContentId(), contentDo.getContent(), articleId);
        return contentVO;
    }

    private static void checkArticleStatus(long userId, ArticleDo articleDo) {
        if (articleDo == null) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }
        if (!CommonStatusEnum.isNormal(articleDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_STATUS_ERROR);
        }
        if (articleDo.getUserId() != userId) {
            throw new ServiceException(GlobalErrorCodes.LOCKED);
        }
    }
}
