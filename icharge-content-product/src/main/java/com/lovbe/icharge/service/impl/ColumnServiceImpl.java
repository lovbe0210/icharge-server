package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.config.ServiceProperties;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.*;
import com.lovbe.icharge.common.model.vo.DirNodeVo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.dao.ArticleDao;
import com.lovbe.icharge.dao.ColumnDao;
import com.lovbe.icharge.dao.ContentDao;
import com.lovbe.icharge.dao.CreateRecordDao;
import com.lovbe.icharge.entity.dto.ColumnDTO;
import com.lovbe.icharge.entity.dto.ColumnOperateDTO;
import com.lovbe.icharge.entity.dto.CreateColumnDTO;
import com.lovbe.icharge.common.model.dto.CreateRecordDo;
import com.lovbe.icharge.entity.vo.ArticleVo;
import com.lovbe.icharge.entity.vo.ColumnVo;
import com.lovbe.icharge.service.ArticleService;
import com.lovbe.icharge.service.ColumnService;
import com.lovbe.icharge.service.feign.StorageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
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
    @Resource
    private ArticleDao articleDao;
    @Resource
    private ContentDao contentDao;
    @Resource
    private CreateRecordDao createRecordDao;
    @Resource
    private CommonService commonService;
    @Resource
    private ArticleService articleService;
    @Resource
    private ServiceProperties serviceProperties;

    @Override
    public ColumnVo createColumn(CreateColumnDTO data, long userId) {
        ColumnDo columnDo = new ColumnDo();
        columnDo.setUid(YitIdHelper.nextId());
        columnDo.setUri(commonService.getBeautifulId())
                .setTitle(data.getTitle())
                .setSynopsis(data.getSynopsis())
                .setIsPublic(data.getIsPublic())
                .setUserId(userId);
        columnDao.insert(columnDo);
        ColumnVo columnVo = new ColumnVo();
        BeanUtil.copyProperties(columnDo, columnVo);
        // 如果是公开专栏，将专栏同步到es中
        if (data.getIsPublic() == 1) {
            ColumnEsEntity esEntity = new ColumnEsEntity()
                    .setUid(columnDo.getUid())
                    .setTitle(columnDo.getTitle())
                    .setIsPublic(columnDo.getIsPublic())
                    .setSynopsis(columnDo.getSynopsis());
            commonService.updateElasticsearchColumn(Arrays.asList(esEntity));
        }
        // 发布创建记录
        CreateRecordDo recordDo = new CreateRecordDo(SysConstant.TARGET_TYPE_ESSAY, columnDo.getUserId());
        recordDo.setUid(columnDo.getUid())
                .setStatus(CommonStatusEnum.NORMAL.getStatus())
                .setCreateTime(new Date())
                .setUpdateTime(recordDo.getCreateTime());
        createRecordDao.insertOrUpdate(recordDo);
        return columnVo;
    }

    @Override
    public ColumnVo getColumnForEdit(Long columnId, long userId) {
        ColumnDo columnDo = columnDao.selectById(columnId);
        checkColumnStatus(userId, columnDo);
        ColumnVo columnVo = new ColumnVo();
        BeanUtil.copyProperties(columnDo, columnVo);
        // 获取专栏内的文章
        List<ArticleDo> selectedList = articleDao.selectList(new LambdaQueryWrapper<ArticleDo>()
                .eq(ArticleDo::getColumnId, columnId)
                .eq(ArticleDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
        columnVo.setArticleList(selectedList);
        return columnVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateColumn(ColumnDTO columnDTO, long userId) {
        ColumnDo columnDo = columnDao.selectById(columnDTO.getUid());
        checkColumnStatus(userId, columnDo);
        columnDo.setUpdateTime(new Date());
        Integer isPublic = null;
        if (columnDTO.getIsPublic() != null && !Objects.equals(columnDTO.getIsPublic(), columnDo.getIsPublic())) {
            isPublic = columnDTO.getIsPublic();
        }
        ColumnEsEntity columnEsEntity = new ColumnEsEntity()
                .setUid(columnDo.getUid())
                .setIsPublic(columnDo.getIsPublic())
                .setTitle(columnDo.getTitle())
                .setSynopsis(columnDo.getSynopsis());
        BeanUtil.copyProperties(columnDTO, columnDo);
        // 判断是否需要更新封面文件
        if (columnDTO.getCoverFile() != null) {
            // 上传文件
            CommonUtils.checkUploadFrequencyLimit(String.valueOf(userId),
                    SysConstant.FILE_SCENE_COVER, serviceProperties.getCoverUploadLimit());
            ResponseBean<String> upload = storageService
                    .upload(new FileUploadDTO(columnDTO.getCoverFile(), SysConstant.FILE_SCENE_COVER, String.valueOf(userId)));
            if (!upload.isResult()) {
                log.error("[更新专栏信息] --- 封面上传失败，errorInfo: {}", upload.getMessage());
                throw new ServiceException(ServiceErrorCodes.ARTICLE_INFO_UPDATE_FAILED);
            }
            columnDo.setCoverUrl(upload.getData());
        }
        columnDao.updateById(columnDo);
        // 更新专栏信息到es中
        if (columnDTO.getIsPublic() != null) {
            columnEsEntity.setIsPublic(columnDTO.getIsPublic());
        }
        if (columnDTO.getTitle() != null) {
            columnEsEntity.setTitle(columnDo.getTitle());
        }
        if (columnDTO.getSynopsis() != null) {
            columnEsEntity.setSynopsis(columnDTO.getSynopsis());
        }
        commonService.updateElasticsearchColumn(Arrays.asList(columnEsEntity));
        // 如果权限变动，更新所有文章的权限
        if (isPublic != null) {
            // 获取专栏下的文章列表
            List<ArticleDo> selectList = articleDao.selectList(new LambdaQueryWrapper<ArticleDo>()
                    .eq(ArticleDo::getColumnId, columnDTO.getUid())
                    .eq(ArticleDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
            if (CollectionUtils.isEmpty(selectList)) {
                return;
            }
            ArticleDo articleDo = new ArticleDo().setIsPublic(isPublic);
            articleDao.update(articleDo, new LambdaQueryWrapper<ArticleDo>()
                    .eq(ArticleDo::getColumnId, columnDTO.getUid())
                    .eq(ArticleDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
            // 同步更新ES中文章数据
            Integer finalIsPublic = isPublic;
            List<ArticleEsEntity> articleEsEntityList = selectList.stream()
                    .map(article -> new ArticleEsEntity()
                            .setUid(article.getUid())
                            .setIsPublic(finalIsPublic))
                    .collect(Collectors.toList());
            commonService.updateElasticsearchArticle(articleEsEntityList);
        }
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteColumnInfo(ColumnDTO columnDTO, long userId) {
        ColumnDo columnDo = columnDao.selectById(columnDTO.getUid());
        checkColumnStatus(userId, columnDo);
        columnDo.setStatus(CommonStatusEnum.DELETE.getStatus());
        columnDao.updateById(columnDo);
        // 删除es中的专栏数据
        commonService.deleteElasticsearchColumn(Arrays.asList(String.valueOf(columnDTO.getUid())));
        // 获取专栏内的文章
        List<ArticleDo> selectList = articleDao.selectList(new LambdaQueryWrapper<ArticleDo>()
                .eq(ArticleDo::getColumnId, columnDTO.getUid())
                .eq(ArticleDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
        if (CollectionUtils.isEmpty(selectList)) {
            return;
        }
        ArticleDo articleDo = new ArticleDo();
        articleDo.setStatus(CommonStatusEnum.DELETE.getStatus());
        articleDao.update(articleDo, new LambdaQueryWrapper<ArticleDo>()
                .eq(ArticleDo::getColumnId, columnDo.getUid()));
        // 同步删除es中的文章数据
        List<String> ids = selectList.stream()
                .map(article -> String.valueOf(article.getUid()))
                .collect(Collectors.toList());
        commonService.deleteElasticsearchArticle(ids);
    }

    @Override
    public JSONArray getColumnDir(Long columnId, long userId) {
        ColumnVo columnForEdit = getColumnForEdit(columnId, userId);
        Long dirContentId = columnForEdit.getDirContentId();
        List<ArticleDo> articleList = columnForEdit.getArticleList();
        Map<Long, ArticleDo> articleMap = CollectionUtils.isEmpty(articleList) ? Collections.EMPTY_MAP : articleList.stream()
                .collect(Collectors.toMap(ArticleDo::getUid, Function.identity()));
        ContentDo contentDo = contentDao.selectById(dirContentId);
        if (contentDo != null && StringUtils.hasLength(contentDo.getContent())) {
            String content = contentDo.getContent();
            try {
                JSONArray parseArray = JSONUtil.parseArray(content);
                if (CollectionUtils.isEmpty(parseArray) && CollectionUtils.isEmpty(articleList)) {
                    return new JSONArray();
                }
                JSONArray finalParseArray = parseArray;
                if (CollectionUtils.isEmpty(parseArray) && !CollectionUtils.isEmpty(articleList)) {
                    articleList.forEach(article -> {
                        DirNodeVo dirNode = new DirNodeVo()
                                .setUid(article.getUid())
                                .setType(1)
                                .setTitle(article.getTitle())
                                .setCreateTime(article.getCreateTime())
                                .setUpdateTime(article.getUpdateTime());
                        finalParseArray.add(dirNode);
                    });
                    contentDo.setContent(JSONUtil.toJsonStr(finalParseArray));
                    contentDao.updateById(contentDo);
                    return finalParseArray;
                }
                Iterator<Object> iterator = parseArray.iterator();
                while (iterator.hasNext()) {
                    Object node = iterator.next();
                    checkNodeInfo((JSONObject) node, iterator, articleMap);
                }
                // 判断是否还有新建的文章没有添加到目录中去
                if (articleMap.size() > 0) {
                    articleMap.values().stream()
                            .sorted((o1, o2) -> o1.getUpdateTime().compareTo(o2.getUpdateTime()))
                            .peek(article -> {
                                DirNodeVo dirNode = new DirNodeVo()
                                        .setUid(article.getUid())
                                        .setType(1)
                                        .setTitle(article.getTitle())
                                        .setCreateTime(article.getCreateTime())
                                        .setUpdateTime(article.getUpdateTime());
                                ;
                                finalParseArray.add(0, dirNode);
                            })
                            .collect(Collectors.toList());
                }

                // 更新目录到数据库
                contentDo.setContent(JSONUtil.toJsonStr(parseArray));
                contentDao.updateById(contentDo);
                return parseArray;
            } catch (Exception e) {
                log.error("[获取专栏目录] --- 目录json解析异常，错误数据：{}", content);
            }
        }
        return new JSONArray();
    }

    public void checkNodeInfo(JSONObject node, Iterator<Object> iterator, Map<Long, ArticleDo> articleMap) {
        if (node.getInt("type") == 1) {
            // 文章节点
            Long uid = node.getLong("uid");
            ArticleDo articleDo = articleMap.get(uid);
            if (articleDo == null) {
                // 文章已被删除或封禁
                iterator.remove();
            } else {
                node.set("title", articleDo.getTitle())
                        .set("createTime", articleDo.getCreateTime())
                        .set("updateTime", articleDo.getUpdateTime());
                articleMap.remove(uid);
            }
        } else if (node.getInt("type") == 2) {
            JSONArray children = node.getJSONArray("children");
            if (CollectionUtils.isEmpty(children)) {
                return;
            }
            Iterator<Object> childrenIterator = children.iterator();
            while (childrenIterator.hasNext()) {
                Object child = childrenIterator.next();
                checkNodeInfo((JSONObject) child, childrenIterator, articleMap);
            }
        }
    }

    @Override
    public Long updateColumnDir(ColumnDTO columnDTO, long userId) {
        ColumnDo columnDo = columnDao.selectById(columnDTO.getUid());
        checkColumnStatus(userId, columnDo);
        Long dirContentId = columnDo.getDirContentId();
        if (dirContentId == null) {
            dirContentId = YitIdHelper.nextId();
        }
        // 更新目录信息
        ContentDo contentDo = new ContentDo();
        contentDo.setUid(dirContentId)
                .setCreateTime(new Date())
                .setUpdateTime(new Date())
                .setStatus(CommonStatusEnum.NORMAL.getStatus());
        contentDo.setContent(columnDTO.getDirContent());
        contentDao.insertOrUpdate(contentDo);
        // 更新专栏信息
        columnDo.setUpdateTime(new Date());
        columnDo.setDirContentId(dirContentId);
        columnDao.updateById(columnDo);
        return dirContentId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchOperate(BaseRequest<ColumnOperateDTO> columnRequest, long userId) {
        // 文章状态校验
        ColumnOperateDTO data = columnRequest.getData();
        List<ArticleDo> selectedList = articleDao.selectList(new LambdaQueryWrapper<ArticleDo>()
                .in(ArticleDo::getUid, data.getArticleList()));
        List<String> articleIds = new ArrayList<>();
        selectedList = selectedList.stream()
                .peek(articleDo -> articleIds.add(String.valueOf(articleDo.getUid())))
                .filter(articleDo -> Objects.equals(articleDo.getUserId(), userId)
                        && CommonStatusEnum.isNormal(articleDo.getStatus()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(selectedList)) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }

        // 批量发布
        if (SysConstant.ARTICLE_BATCH_PUBLISH.equals(data.getOperateType())) {
            articleService.batchPublish(selectedList);
            return;
        }

        // 批量移出专栏
        if (SysConstant.ARTICLE_BATCH_REMOVE.equals(data.getOperateType())) {
            articleDao.batchUpdate(selectedList, data.getColumnId(), null, data.getOperateType());
            return;
        }

        // 批量删除文章
        if (SysConstant.ARTICLE_BATCH_DELETE.equals(data.getOperateType())) {
            articleDao.batchUpdate(selectedList, data.getColumnId(), null, data.getOperateType());
            commonService.deleteElasticsearchArticle(articleIds);
            return;
        }

        // 批量导出文章
        if (SysConstant.ARTICLE_BATCH_EXPORT.equals(data.getOperateType())) {
            //TODO 导出
        }
    }

    @Override
    public List<ArticleVo> getColumnArticleList(Long columnId, long userId) {
        List<ArticleDo> selectedList = articleDao.selectList(new LambdaQueryWrapper<ArticleDo>()
                .eq(ArticleDo::getColumnId, columnId)
                .eq(ArticleDo::getStatus, CommonStatusEnum.NORMAL.getStatus())
                .orderByDesc(ArticleDo::getUpdateTime));
        if (CollectionUtils.isEmpty(selectedList)) {
            return List.of();
        }
        return selectedList.stream().map(articleDo -> {
            ArticleVo articleVO = new ArticleVo();
            BeanUtil.copyProperties(articleDo, articleVO);
            return articleVO;
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
