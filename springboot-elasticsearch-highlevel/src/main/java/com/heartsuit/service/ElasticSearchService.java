package com.heartsuit.service;

import org.elasticsearch.common.xcontent.XContentType;

import java.util.List;
import java.util.Map;

/**
 * @Author Heartsuit
 * @Date 2020-06-04
 */
public interface ElasticSearchService {
    /**
     * 创建索引
     * @param index
     * @return
     */
    boolean createIndex(String index);

    /**
     * 删除索引
     * @param index
     * @return
     */
    boolean deleteIndex(String index);

    /**
     * 插入文档
     * @param index
     * @param object
     * @param json
     * @param id
     * @return
     */
    int insertDocument(String index, Object object, XContentType json, String id);

    /**
     * 根据id获取文档
     * @param index
     * @param id
     * @param mappingClass
     * @param <T>
     * @return
     */
    <T> T getDocument(String index, String id, Class<T> mappingClass);

    /**
     * 根据id更新文档
     * @param index
     * @param object
     * @param json
     * @param id
     * @return
     */
    int updateDocument(String index, Object object, XContentType json, String id);

    /**
     * 根据id删除文档
     * @param index
     * @param id
     * @return
     */
    int deleteDocument(String index, String id);

    /**
     * 查询数据
     * @param index
     * @return
     */
    List<Map<String, Object>> searchDocument(String index);

    /**
     * 批量插入文档
     * @param index
     * @param list
     * @param dataType
     * @return
     */
    boolean batchInsertDocument(String index, List<?> list, XContentType dataType);
}
