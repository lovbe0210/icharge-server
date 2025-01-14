package com.lovbe.icharge.config;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.dto.ArticleEsEntity;
import com.lovbe.icharge.common.model.dto.ColumnEsEntity;
import com.lovbe.icharge.common.model.dto.UserEsEntity;
import com.lovbe.icharge.common.util.ElasticSearchUtils;
import com.lovbe.icharge.common.util.JsonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/14 22:59
 * @Description: MS
 */
@Slf4j
@Component
public class ElasticsearchIndexInit implements InitializingBean {
    @Resource
    private RestHighLevelClient highLevelClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化Elasticsearch索引
        GetIndexRequest getIndexReq = new GetIndexRequest(SysConstant.ES_INDEX_USER);
        boolean exists = highLevelClient.indices().exists(getIndexReq, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest request = new CreateIndexRequest(SysConstant.ES_INDEX_USER);
            Map<String, Object> indexSource = ElasticSearchUtils.getIndexSource(UserEsEntity.class);
            String json = JsonUtils.toJsonString(indexSource);
            request.source(json, XContentType.JSON);
            CreateIndexResponse response = highLevelClient.indices().create(request, RequestOptions.DEFAULT);
            String index = response.index();
            if (log.isDebugEnabled()) {
                log.debug("[初始化elasticsearch] --- created index: {}", index);
            }
        }

        getIndexReq = new GetIndexRequest(SysConstant.ES_INDEX_ARTICLE);
        exists = highLevelClient.indices().exists(getIndexReq, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest request = new CreateIndexRequest(SysConstant.ES_INDEX_ARTICLE);
            Map<String, Object> indexSource = ElasticSearchUtils.getIndexSource(ArticleEsEntity.class);
            String json = JsonUtils.toJsonString(indexSource);
            request.source(json, XContentType.JSON);
            CreateIndexResponse response = highLevelClient.indices().create(request, RequestOptions.DEFAULT);
            String index = response.index();
            if (log.isDebugEnabled()) {
                log.debug("[初始化elasticsearch] --- created index: {}", index);
            }
        }

        getIndexReq = new GetIndexRequest(SysConstant.ES_INDEX_COLUMN);
        exists = highLevelClient.indices().exists(getIndexReq, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest request = new CreateIndexRequest(SysConstant.ES_INDEX_COLUMN);
            Map<String, Object> indexSource = ElasticSearchUtils.getIndexSource(ColumnEsEntity.class);
            String json = JsonUtils.toJsonString(indexSource);
            request.source(json, XContentType.JSON);
            CreateIndexResponse response = highLevelClient.indices().create(request, RequestOptions.DEFAULT);
            String index = response.index();
            if (log.isDebugEnabled()) {
                log.debug("[初始化elasticsearch] --- created index: {}", index);
            }
        }
    }
}
