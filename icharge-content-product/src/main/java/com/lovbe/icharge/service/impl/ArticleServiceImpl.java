package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.entity.dto.ArticleDo;
import com.lovbe.icharge.entity.vo.ArticleVO;
import com.lovbe.icharge.mapper.ArticleMapper;
import com.lovbe.icharge.service.ArticleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/9 23:54
 * @Description: MS
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;

    @Override
    public ArticleVO createBlankDoc(long userId) {
        ArticleDo articleDo = new ArticleDo();
        articleDo.setUid(YitIdHelper.nextId())
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        articleDo.setUserId(userId).setTitle("无标题文档");
        articleMapper.insertOrUpdate(articleDo);
        ArticleVO articleVO = new ArticleVO();
        BeanUtil.copyProperties(articleDo, articleVO);
        return articleVO;
    }
}
