package com.lovbe.icharge.common.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.lovbe.icharge.common.config.AIPromptProperties;
import com.lovbe.icharge.common.dao.CommonDao;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.KafkaMessage;
import com.lovbe.icharge.common.model.dto.*;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.ElasticSearchUtils;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.common.util.SpringContextUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.ai.chat.ChatClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/20 20:20
 * @Description: 公共服务
 */
@Slf4j
@Lazy
@Service
public class CommonServiceImpl implements CommonService {
    @Resource
    private CommonDao commonDao;
    @Resource
    private ChatClient chatClient;
    @Resource
    private AIPromptProperties aiPromptProperties;

    @Override
    public UserInfoDo getCacheUser(Long userId) {
        String cacheUserKey = RedisKeyConstant.getCacheUserKey(userId);
        UserInfoDo cacheUser = (UserInfoDo) RedisUtil.get(cacheUserKey);
        if (cacheUser == null) {
            // 获取用户信息
            CacheUserDo cacheUserDo = commonDao.getUserById(userId);
            UserInfoDo aDo = new UserInfoDo();
            BeanUtil.copyProperties(cacheUserDo, aDo);
            if (cacheUserDo != null && StringUtils.hasLength(cacheUserDo.getTagsStr())) {
                List<Map> lists = JsonUtils.parseArray(cacheUserDo.getTagsStr(), Map.class);
                aDo.setTags(lists);
            }
            cacheUser = CommonUtils.checkUserStatus(aDo);
            RedisUtil.set(cacheUserKey, cacheUser, SysConstant.DAY_30);
        } else {
            RedisUtil.expire(cacheUserKey, SysConstant.DAY_30);
        }
        return cacheUser;
    }

    /**
     * @description 发送kafka消息
     * @param[1] historyDo
     * @author lovbe0210
     * @date 2024/11/24 0:10
     */
    public <T> void sendMessage(String appName, String topic, T t) {
        KafkaMessage<T> message = new KafkaMessage<>(appName, topic, t);
        try {
            KafkaTemplate kafkaTemplate = SpringContextUtils.getBean(KafkaTemplate.class);
            CompletableFuture send = kafkaTemplate.send(topic, JSONUtil.toJsonStr(message));
            send.thenAccept(result -> {
                log.info("[send-message]--消息发送成功， sid：{}", message.getMsgId());
            }).exceptionally(ex -> {
                log.error("[send-message]--消息发送失败，cause: {}, sendData: {}", ex.toString(), JSONUtil.toJsonStr(message));
                return null;
            });
        } catch (Exception e) {
            log.error("[send-message]--消息发送失败，kafka服务不可用, sendData: {}, errorInfo: {}", JSONUtil.toJsonStr(message), e.toString());
        }
    }

    @Override
    public List<MenuDTO> getMenuList() {
        return commonDao.selectList();
    }

    @Override
    public String getBeautifulId() {
        String beautifulIdKey = RedisKeyConstant.getBeautifulKey();
        String beautifulId = IdUtil.nanoId(6);
        boolean hasKey = RedisUtil.hasKey(beautifulIdKey);
        Long expire = RedisUtil.getExpire(beautifulIdKey);
        // key不存在或者key存续时间小于60s都认为key不存在，获取数据库最新数据
        if (!hasKey || expire == null || expire < 60) {
            // 获取数据库中当前user下的所有beautifulId
            List<String> uriList = commonDao.selectAllUri();
            if (CollectionUtils.isEmpty(uriList)) {
                uriList = List.of("lovbe");
            }
            RedisUtil.hputAll(beautifulIdKey,
                    uriList.stream().collect(Collectors.toMap(Function.identity(), Function.identity())));
            RedisUtil.expire(beautifulIdKey, RedisKeyConstant.EXPIRE_7_DAY);
        }
        boolean hsetted = RedisUtil.hsetIfAbsent(beautifulIdKey, beautifulId, null);
        while (!hsetted) {
            beautifulId = IdUtil.nanoId(6);
            hsetted = RedisUtil.hsetIfAbsent(beautifulIdKey, beautifulId, null);
        }
        return beautifulId;
    }

    @Override
    public AIAuditResultDTO sendAuditChat(String textValue) {
        String message = """
                {
                  "messages": [
                    {"role": "system", "content": "%s"},
                    {"role": "user", "content": "%s"}
                  ],
                  response_format={"type": "json_object"}
                }
                """.formatted(aiPromptProperties.getAuditPromptContent(), textValue);
        AIAuditResultDTO resultDTO = null;
        try {
            String call = this.chatClient.call(message);
           /* String call = """
                    {"result": true}
                    """;*/
            if (log.isDebugEnabled()) {
                log.debug("kimi返回审核结果: {}", call);
            }
            if (StringUtils.hasLength(call)) {
                call = call.replace("```json", "");
                call = call.replace("```", "");
            }
            resultDTO = JSONUtil.toBean(call, AIAuditResultDTO.class);
        } catch (Exception e) {
            // kimi有一类异常是文本本身就违规或输出内容违规，需要转换此类异常为审核失败
            String errorMsg = e.toString();
            if (errorMsg != null && errorMsg.indexOf(SysConstant.KIMI_FAILED_TYPE) != -1) {
                resultDTO = new AIAuditResultDTO(false, Arrays.asList("文章内容可能包含不安全或敏感内容"), null);
            } else {
                log.error("[kimi请求失败] --- errorInfo: {}", errorMsg);
            }
        }
        return resultDTO;
    }

    public AIPortraitTagResultDTO sendPortraitTagChat(String tags) {
        String message = """
                {
                  "messages": [
                    {"role": "system", "content": "%s"},
                    {"role": "user", "content": "%s"}
                  ],
                  response_format={"type": "json_object"}
                }
                """.formatted(aiPromptProperties.getPortraitTagPromptContent(), tags);
        AIPortraitTagResultDTO resultDTO = null;
        try {
            String call = this.chatClient.call(message);
           /* String call = """
                    {"result": true}
                    """;*/
            if (log.isDebugEnabled()) {
                log.debug("kimi返回人物画像: {}", call);
            }
            if (StringUtils.hasLength(call)) {
                call = call.replace("```json", "");
                call = call.replace("```", "");
            }
            resultDTO = JSONUtil.toBean(call, AIPortraitTagResultDTO.class);
        } catch (Exception e) {
            // kimi有一类异常是文本本身就违规或输出内容违规，需要转换此类异常为画像失败
            String errorMsg = e.toString();
            if (errorMsg != null && errorMsg.indexOf(SysConstant.KIMI_FAILED_TYPE) != -1) {
                resultDTO = new AIPortraitTagResultDTO(false, null, null);
            } else {
                log.error("[kimi请求失败] --- errorInfo: {}", errorMsg);
            }
        }
        return resultDTO;
    }

    public void updateElasticsearchArticle(ArticleEsEntity articleEsEntity) throws IOException {
        // 判断是否存在索引
        String indexName = ElasticSearchUtils.getIndexName(articleEsEntity.getClass());
        GetIndexRequest getIndexReq = new GetIndexRequest(indexName);
        RestHighLevelClient highLevelClient = SpringContextUtils.getBean(RestHighLevelClient.class);
        boolean exists = highLevelClient.indices().exists(getIndexReq, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            Map<String, Object> indexSource = ElasticSearchUtils.getIndexSource(articleEsEntity.getClass());
            String json = JsonUtils.toJsonString(indexSource);
            request.source(json, XContentType.JSON);
            CreateIndexResponse response = highLevelClient.indices().create(request, RequestOptions.DEFAULT);
            String index = response.index();
            if (log.isDebugEnabled()) {
                log.debug("[更新elasticsearch数据] --- created index: {}", index);
            }
        }
        // 插入或更新数据
        String jsonValue = JsonUtils.toJsonString(articleEsEntity);
        UpdateRequest request = new UpdateRequest(indexName, String.valueOf(articleEsEntity.getUid()))
                // 如果不存在需要插入的内容
                .doc(jsonValue, XContentType.JSON)
                // 如果存在需要更新的内容
                .upsert(jsonValue, XContentType.JSON);
        UpdateResponse updateResponse = highLevelClient.update(request, RequestOptions.DEFAULT);
        int status = updateResponse.status().getStatus();
        if (log.isDebugEnabled()) {
            log.debug("[更新elasticsearch数据] --- update article to elasticsearch, articleId: {}, resultStatus: {}",
                    articleEsEntity.getUid(), status);
        }
    }

    public void updateElasticsearchUser(UserEsEntity userEsEntity) throws IOException {
        // 判断是否存在索引
        String indexName = ElasticSearchUtils.getIndexName(userEsEntity.getClass());
        GetIndexRequest getIndexReq = new GetIndexRequest(indexName);
        RestHighLevelClient highLevelClient = SpringContextUtils.getBean(RestHighLevelClient.class);
        boolean exists = highLevelClient.indices().exists(getIndexReq, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            Map<String, Object> indexSource = ElasticSearchUtils.getIndexSource(userEsEntity.getClass());
            String json = JsonUtils.toJsonString(indexSource);
            request.source(json, XContentType.JSON);
            CreateIndexResponse response = highLevelClient.indices().create(request, RequestOptions.DEFAULT);
            String index = response.index();
            if (log.isDebugEnabled()) {
                log.debug("[更新elasticsearch数据] --- created index: {}", index);
            }
        }
        // 插入或更新数据
        String jsonValue = JsonUtils.toJsonString(userEsEntity);
        UpdateRequest request = new UpdateRequest(indexName, String.valueOf(userEsEntity.getUid()))
                // 如果不存在需要插入的内容
                .doc(jsonValue, XContentType.JSON)
                // 如果存在需要更新的内容
                .upsert(jsonValue, XContentType.JSON);
        UpdateResponse updateResponse = highLevelClient.update(request, RequestOptions.DEFAULT);
        int status = updateResponse.status().getStatus();
        if (log.isDebugEnabled()) {
            log.debug("[更新elasticsearch数据] --- update user to elasticsearch, userId: {}, resultStatus: {}",
                    userEsEntity.getUid(), status);
        }
    }
}
