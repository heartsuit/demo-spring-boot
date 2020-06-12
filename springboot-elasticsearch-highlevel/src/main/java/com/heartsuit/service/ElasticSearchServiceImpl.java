package com.heartsuit.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Heartsuit
 * @Date 2020-06-04
 */
@Slf4j
@Component
public class ElasticSearchServiceImpl implements ElasticSearchService {
    private final RestHighLevelClient client;

    public ElasticSearchServiceImpl(RestHighLevelClient client) {
        this.client = client;
    }

    /**
     * 创建索引
     *
     * @param index 必须小写,格式参考文档，否则报错：Elasticsearch exception [type=invalid_index_name_exception, reason=Invalid index name [OK], must be lowercase]
     * @return 是否创建成功
     */
    @Override
    public boolean createIndex(String index) {
        CreateIndexResponse response;
        try {
            if (!this.existsIndex(index)) {
                response = client.indices().create(new CreateIndexRequest(index), RequestOptions.DEFAULT);
            } else {
                return true;//索引已存在
            }
        } catch (Exception e) {
            log.error("ElasticSearch 创建索引异常：{}", e.getMessage());
            return false;
        }
        return response.isAcknowledged();
    }

    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     */
    private boolean existsIndex(String index) throws IOException {
        return client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
    }

    /**
     * 删除索引
     *
     * @param index 必须小写,格式参考文档，否则：找不到大写索引名
     * @return 是否删除成功
     */
    @Override
    public boolean deleteIndex(String index) {
        AcknowledgedResponse response = null;
        try {
            if (this.existsIndex(index)) {
                response = client.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
            } else {
                return true;//索引不存在
            }
        } catch (Exception e) {
            log.error("ElasticSearch 删除索引异常：{}", e.getMessage());
            return false;
        }
        return response.isAcknowledged();
    }

    /**
     * 创建文档
     * id相同则更新、不同则创建,数据格式（字段）不同则空,字段为追加模式
     *
     * @param index    索引
     * @param data     数据
     * @param dataType 格式类型    例:XContentType.JSON
     * @param id       唯一标识   put /index/1
     * @return
     */
    @Override
    public int insertDocument(String index, Object data, XContentType dataType, String id) {
        IndexRequest request = new IndexRequest(index);
        request.id(id);
        String dataString = JSONObject.toJSONString(data);
        request.source(dataString, dataType);
        IndexResponse response = null;
        try {
            response = client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("ElasticSearch 创建文档异常：{}", e.getMessage());
        }
        return response != null ? response.status().getStatus() : 400;
    }

    /**
     * 获取文档
     *
     * @param index
     * @param id
     * @param mappingClass
     * @param <T>
     * @return
     */
    @Override
    public <T> T getDocument(String index, String id, Class<T> mappingClass) {
        GetResponse getResponse = null;
        try {
            if (this.existsIndex(index)) {
                GetRequest getRequest = new GetRequest(index, id);
                getResponse = client.get(getRequest, RequestOptions.DEFAULT);
                String sourceAsString = getResponse.getSourceAsString();

                if (sourceAsString == null || sourceAsString.isEmpty()) {
                    return null;
                }
                /**Jackson日期时间序列化问题：
                 * Cannot construct instance of `java.time.LocalDateTime` (no Creators, like default constructor, exist): no String-argument constructor/factory method to deserialize from String value ('2020-06-04 15:07:54')
                 */
//                ObjectMapper objectMapper = new ObjectMapper();
//                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//                objectMapper.registerModule(new JavaTimeModule());
//                T result = objectMapper.readValue(sourceAsString, mappingClass);
                T result = JSON.parseObject(sourceAsString, mappingClass);
                return result;
            }
        } catch (Exception e) {
            log.error("ElasticSearch 获取文档异常：{}", e.getMessage());
        }
        return null;
    }

    /**
     * 更新文档信息
     *
     * @param index
     * @param data
     * @param dataType
     * @param id
     * @return
     */
    @Override
    public int updateDocument(String index, Object data, XContentType dataType, String id) {
        UpdateResponse updateResponse = null;
        try {
            if (this.existsIndex(index)) {
                UpdateRequest updateRequest = new UpdateRequest(index, id);
                String dataString = JSONObject.toJSONString(data);
                updateRequest.doc(dataString, dataType);
                updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            log.error("ElasticSearch 更新文档异常：{}", e.getMessage());
        }
        return updateResponse != null ? updateResponse.status().getStatus() : 400;
    }

    /**
     * 删除文档
     *
     * @param index
     * @param id
     * @return
     */
    @Override
    public int deleteDocument(String index, String id) {
        DeleteResponse deleteResponse = null;
        try {
            if (this.existsIndex(index)) {
                DeleteRequest deleteRequest = new DeleteRequest(index, id);
                deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            log.error("ElasticSearch 删除文档异常：{}", e.getMessage());
        }
        return deleteResponse != null ? deleteResponse.status().getStatus() : 400;
    }

    /**
     * 批量操作文档信息
     * 备注:暂局限入参list,可扩展其他<?>
     *
     * @param index
     * @param list     标识相同则覆盖,否则新增
     * @param dataType
     * @return
     */
    @Override
    public boolean batchInsertDocument(String index, List<?> list, XContentType dataType) {
        BulkRequest bulkRequest = new BulkRequest();
        for (Object obj : list) {
            // 自动生成id
            bulkRequest.add(new IndexRequest(index).source(JSON.toJSONString(obj), dataType));
        }
        BulkResponse bulk = null;
        try {
            bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("ElasticSearch批量操作文档信息异常：{}", e.getMessage());
        }
        return bulk != null && !bulk.hasFailures();
    }

    /**
     * 查询数据
     * 备注：可拓展深入精准查询、范围查询、模糊查询、匹配所有等
     *
     * @param index
     * @return
     */
    @Override
    public List<Map<String, Object>> searchDocument(String index) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        MatchAllQueryBuilder termQueryBuilder = QueryBuilders.matchAllQuery();
        sourceBuilder.query(termQueryBuilder);
        // sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse search;
        try {
            search = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("ElasticSearch 查询数据异常：{}", e.getMessage());
            return null;
        }
        SearchHit[] hits = search.getHits().getHits();
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (SearchHit hit : hits) {
            mapList.add(hit.getSourceAsMap());
        }
        return mapList;
    }

    private void close(RestHighLevelClient client) {
        try {
            client.close();
        } catch (IOException e) {
            log.error("ElasticSearch 关闭异常：{}", e.getMessage());
        }
    }
}
