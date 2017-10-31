package com.niewj.util.es;

import com.niewj.util.JSONUtil;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by weijun.nie on 2017/5/19.
 */
public class ElasticUtil {

    private static Logger logger = LoggerFactory.getLogger(ElasticUtil.class);
    /**
     * client一定要是单例
     */
    private static Client client = null;

    /**
     * 创建es client 一定要是单例，单例，单例！不要在应用中构造多个客户端！
     *
     * @param clusterName 集群名字
     * @param nodeIp      集群中节点的ip地址
     * @param nodePort    节点的端口
     * @return
     * @throws UnknownHostException
     */
    public static synchronized Client getClient(String clusterName, String nodeIp, int nodePort) throws UnknownHostException {
        //设置集群的名字
        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", clusterName)
                .put("client.transport.sniff", false)
//                .put("number_of_shards", 1)
//                .put("number_of_replicas", 0)
                .build();

        //创建集群client并添加集群节点地址
        Client client = TransportClient.builder().settings(settings).build()
//                .addTransportAddress(new InetSocketTransportAddress("192.168.200.195", 9370))
//                .addTransportAddress(new InetSocketTransportAddress("192.168.200.196", 9370))
//                .addTransportAddress(new InetSocketTransportAddress("192.168.200.197", 9370))
//                .addTransportAddress(new InetSocketTransportAddress("192.168.200.198", 9370))
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(nodeIp), nodePort));

        return client;
    }

    /**
     * 创建索引
     * 注意：在生产环节中通知es集群的owner去创建index
     *
     * @param indexName 对应库名
     * @param typeName  对应表名
     * @throws IOException
     */
    private static void createIndex(String indexName, String typeName, XContentBuilder mapping) throws IOException {
        final IndicesExistsResponse iRes = client.admin().indices().prepareExists(indexName).execute().actionGet();
        if (iRes.isExists()) {
            logger.info("删除索引：{}", indexName);
            client.admin().indices().prepareDelete(indexName).execute().actionGet();
        }

        client.admin().indices().prepareCreate(indexName).setSettings(Settings.settingsBuilder().put("number_of_shards", 5).put("number_of_replicas", "0")).execute().actionGet();

        client.admin().indices()
                .preparePutMapping(indexName)
                .setType(typeName)
                .setSource(mapping)
                .execute().actionGet();
    }

    /**
     * 用java字符串创建document
     * 指定索引名称，type名称和documentId(documentId可选，不设置则系统自动生成)创建document
     *
     * @param indexName 对应库名
     * @param typeName  对应表名
     * @param jsonObj   对应记录对象-json串
     */
    private static void indexWithJsonString(String indexName, String typeName, String jsonObj) {
        IndexResponse response = client.prepareIndex(indexName, typeName, "1")
                .setSource(jsonObj)
                .execute()
                .actionGet();
        logger.info("IndexResponse {}", JSONUtil.safeToJson(response));
    }

    /**
     * 用java的map构建document
     *
     * @param indexName 对应库名
     * @param typeName  对应表名
     * @param docMap    对应记录字段
     */
    private static void indexWithJavaMap(String indexName, String typeName, Map<String, Object> docMap) {
        //指定索引名称，type名称和documentId(documentId可选，不设置则系统自动生成)创建document
        IndexResponse response = client.prepareIndex(indexName, typeName, "2")
                .setSource(docMap)
                .execute()
                .actionGet();
        //response中返回索引名称，type名称，doc的Id和版本信息
        logger.info("IndexResponse {}", JSONUtil.safeToJson(response));
    }

    /**
     * @param indexName 对应库名
     * @param typeName  对应表名
     * @param id        要修改的文档的ID
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static void updateDoc(String indexName, String typeName, String id, Map<String, Object> updateMap) throws IOException, InterruptedException, ExecutionException {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(indexName);
        updateRequest.type(typeName);
        updateRequest.id(id);

        XContentBuilder xContentBuilder = jsonBuilder().startObject();
        /**
         * 要修改的字段
         */
        for (Map.Entry<String, Object> entry : updateMap.entrySet()) {
            xContentBuilder.field(entry.getKey(), entry.getValue());
        }
        xContentBuilder.endObject();
        updateRequest.doc(xContentBuilder);

        UpdateResponse resp = client.update(updateRequest).get();
        resp.getClass();

        logger.info("IndexResponse {}", JSONUtil.safeToJson(resp));
    }

    /**
     *  为type设置mapping--设置完后-指定的field的类型会限定。
     *  比如brandId 为 integer, 设值20.40就会失败。
     * @param typeName
     * @return
     * @throws IOException
     */
    private static XContentBuilder initMapping(String typeName) throws IOException {
        XContentBuilder mapping = jsonBuilder().startObject().startObject(typeName)
//                     .startObject("_routing").field("path","tid").field("required", "true").endObject()
                .startObject("_source").field("enabled", "true").endObject()
                .startObject("_all").field("enabled", "false").endObject()
                .startObject("properties")
                .startObject("productId").field("store", true).field("type", "long").field("index", "not_analyzed").endObject()
                .startObject("productName").field("store", true).field("type", "string").field("index", "analyzed").field("analyzer", "standard").endObject()
                .startObject("brandId").field("store", true).field("type", "integer").endObject()
                .startObject("brandName").field("store", true).field("type", "string").endObject()
                .startObject("price").field("store", true).field("type", "double").field("index", "not_analyzed").endObject()
//                .startObject("tid").field("store", true).field("type", "string").field("index", "not_analyzed").endObject()
//                .startObject("location").field("store", true).field("type", "geo_point").field("lat_lon", true).field("geohash", true).field("geohash_prefix", true).field("geohash_precision", 7).endObject()
//                .startObject("shape").field("store", true).field("type", "geo_shape").field("geohash", true).field("geohash_prefix", false).field("geohash_precision", 7).endObject()
//                .startObject("endTime").field("type", "date").field("store", true).field("index", "not_analyzed").field("format", "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss.SSSZ").endObject()//2015-08-21T08:35:13.890Z
//                .startObject("date").field("type", "date")
//              .field("store", true)
//              .field("index", "not_analyzed")
                //2015-08-21T08:35:13.890Z
//              .field("format", "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .endObject()
                .endObject()
                .endObject();

        return mapping;
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        String clusterName = "es-cluster";
        String ipS = "127.0.0.1";
        int port = 9301;
        String indexName = "index-1";
        String typeName = "table-1";

        /**
         *  1. 获取client -- 保证只有一份
         */
        client = ElasticUtil.getClient(clusterName, ipS, port);
        logger.info("client cluster: {} , client == {}", clusterName, client);
        logger.info("-------------------------------------");

        /**
         * 2.1 创建索引 - mapping
         */
//        XContentBuilder mapping = initMapping(typeName);
//        logger.info("create index mapping = {}", mapping);

        /**
         * 2.2 创建索引 - 创建1次--除非要删掉重建
         */
//        createIndex(indexName, typeName, mapping);
//        logger.info("create index indexName = {}, typeName = {} ", indexName, typeName);
//        logger.info("-------------------------------------");

        /**
         * 3.1  新增doc -- 1 使用json字符串
         */
//        Product product1 = new Product(1001L, "机械键盘", 20, "键鼠", 400.00);
//        String product1Json = JSONUtil.safeToJson(product1);
//        indexWithJsonString(indexName, typeName, product1Json);
//        logger.info("-------------------------------------");

        /**
         * 3.2  新增doc -- 1 使用java map
         */
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("productId", 10002L);
//        map.put("productName", "显示器");
//        map.put("brandId", 20);
//        map.put("brandName", "dell");
//        map.put("price", 2000.00);
//        map.put("shop", "JD.com"); // 这个是Product类没有的;
//        indexWithJavaMap(indexName, typeName, map);
//        logger.info("-------------------------------------");

        /**
         * 4. 修改doc
         */
        Map<String, Object> updateMap = new HashMap<String, Object>();
        updateMap.put("price", 2200.00);
        updateMap.put("shop", "tmall.com"); // 这个是Product类没有的;
        updateDoc(indexName, typeName, "2", updateMap);

    }
}
