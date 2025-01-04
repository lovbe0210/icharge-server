package com.lovbe.icharge.common.util;

import cn.hutool.core.map.MapUtil;
import com.lovbe.icharge.common.annotation.EsFieldType;
import com.lovbe.icharge.common.annotation.EsIndex;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/5 0:00
 * @Description: MS
 */
public class ElasticSearchUtils {
    // 包含主分片和副分片数量的设置，默认都为1，一般设置主分片数小于等于集群节点数即可，此处单机副分片设为0
    public static final Map<String, Object> settings = Map.of("number_of_shards", 1, "number_of_replicas", 0);
    // id类型
    public static final Map<String, String> typeLong = Map.of("type", "long");
    // 文本类型，在做索引时使用ik_max_word进行最细粒度的拆分，在搜索时对搜索词进行最粗粒度的拆分得到比较准确的结果
    public static final Map<String, String> typeText = Map.of("type", "text", "analyzer", "ik_max_word", "search_analyzer", "ik_smart");


    /**
     * @description: 获取索引字段mapping和设置
     * @param: Class<?>
     * @return: Map<Object>
     * @author: lovbe0210
     * @date: 2025/1/5 1:35
     */
    public static Map<String, Object> getIndexSource(Class<?> clazz) {
        Map<String, Object> properties = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(EsFieldType.class)) {
                EsFieldType fieldAnnotation = field.getAnnotation(EsFieldType.class);
                String value = fieldAnnotation.value();
                if (value != null) value = value.toLowerCase();
                Map<String, String> fieldTypeMap = null;
                switch (value) {
                    case "long":
                        fieldTypeMap = typeLong;
                        break;
                    case "text":
                        fieldTypeMap = new HashMap<>(typeText);
                        if (StringUtils.hasLength(fieldAnnotation.analyzer())) {
                            fieldTypeMap.put("analyzer", fieldAnnotation.analyzer());
                        }
                        if (StringUtils.hasLength(fieldAnnotation.searchAnalyzer())) {
                            fieldTypeMap.put("search_analyzer", fieldAnnotation.searchAnalyzer());
                        }
                        break;
                }
                properties.put(field.getName(), fieldTypeMap);
            }
        }
        // 索引字段映射
        Map<String, Object> mappings = Map.of("properties", properties);
        Map<String, Object> indexSource = Map.of("settings", settings, "mappings", mappings);
        return indexSource;
    }

    /**
     * @description: 获取索引名
     * @param: Class<?>
     * @return: String
     * @author: lovbe0210
     * @date: 2025/1/5 1:35
     */
    public static String getIndexName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(EsIndex.class)) {
            EsIndex indexAnnotation = clazz.getAnnotation(EsIndex.class);
            return indexAnnotation.value();
        }
        throw new IllegalArgumentException("Class does not have @EsIndex annotation");
    }
}
