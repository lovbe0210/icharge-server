package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.entity.dto.ArticleDo;
import com.lovbe.icharge.entity.dto.ColumnDo;
import com.lovbe.icharge.entity.vo.ArticleVO;
import com.lovbe.icharge.mapper.ArticleMapper;
import com.lovbe.icharge.mapper.ColumnMapper;
import com.lovbe.icharge.service.ArticleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/9 23:54
 * @Description: MS
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
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
        ArticleVO articlevo = articleMapper.selectArticleForEdit(userId, articleId);
        if (articlevo == null || !CommonStatusEnum.isNormal(articlevo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }
        if (CommonStatusEnum.isDisable(articlevo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_STATUS_ERROR);
        }
        return articlevo;
    }
}
