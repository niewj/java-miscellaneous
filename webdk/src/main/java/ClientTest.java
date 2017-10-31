import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.stats.StatsBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

public class ClientTest {
    private static Client client = null;//client一定要是单例，单例，单例！不要在应用中构造多个客户端！

    public static void main(String[] args) {

        try {
            //用集群名字，集群节点地址构建es client,注意：client一定要是单例，单例，单例！不要在应用中构造多个客户端！
            client = getClient("jiesi-1", "192.168.200.196", 9303);//client一定要是单例，单例，单例！不要在应用中构造多个客户端！
            //创建indexName:twitter, typeName:tweet的索引
            createIndex("twitter", "tweet");
            //用java字符串在索引twitter,type为tweet中创建document
            indexWithStr("twitter", "tweet");
            //用java map在索引twitter,type为tweet中创建document
            indexWithMap("twitter", "tweet");
            updateDoc("twitter", "tweet", "1");
            // -----------------------------------------------
            //在index:twitter, type:tweet中用document id获取
            indexWithBulk("twitter", "tweet");
            getWithId("twitter", "tweet", "4");
            scrollSearch("twitter", "tweet", "1", "2");
            //多ID查询doc
            searchWithIds("twitter", "tweet", "postDate", "message", "1", "2");
            countWithQuery("twitter", "tweet", "user", "kimchy", "postDate", "message");
            //在index:twitter, type:tweet中做term query查询
            searchWithTermQuery("twitter", "tweet", "user", "101", "kimchy", "message");
            searchWithFilter("twitter", "tweet", "user", "101", "kimchy", "message");
            //sum one field value
            sumOneField("twitter", "tweet", "price");
            sumCountSearch("twitter", "tweet", "price", "tid", "message", "elasticsearch");
            //在index:twitter, type:tweet中做term query查询并返还指定的field
            searchWithTermQueryAndRetureSpecifiedFields("twitter", "tweet", "user", "kimchy", "postDate", "message", "user", "message");
            //在index:twitter, type:tweet中做booean query查询
            searchWithBooleanQuery("twitter", "tweet", "user", "kimchy", "message", "Elasticsearch", "postDate", "message");
            searchWithBooleanFilter("twitter", "tweet", "user", "kimchy", "message", "Elasticsearch", "postDate", "message");
            //在index:twitter, type:tweet中做range query查询
            numericRangeSearch("twitter", "tweet", "price", 6.1, 6.3, "message");
            termRangeSearch("twitter", "tweet", "tid", "10000", "20000", "message");
            dateRangeSearch("twitter", "tweet", "endTime", "2015-08-20 12:27:11", "2015-08-29 14:00:00");
            dateRangeSearch2("twitter", "tweet", "endTime", "2015-08-21T08:35:13.890Z", "2015-08-30T14:00:00.000Z");
            rangeSearchWithOtherSearch("twitter", "tweet", "tid", "10000", "20000", "message");
            //在index:twitter, type:tweet中做fuzzy search查询
            fuzzySearch("twitter", "tweet", "message", "Elastic", "postDate", "message");
            //在index:twitter, type:tweet中做wildcard查询
            wildcardSearch("twitter", "tweet", "message", "El*stic", "postDate", "message");
            //在index:twitter, type:tweet中删除id为1的doc
            deleteDocWithId("twitter", "tweet", "1");
            //search multi indices
            searchMultiIndices("twitter", "twitter2", "tweet", "tweet2", "user", "kimchy");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    private static void searchMultiIndices(String indexName1, String indexName2,
                                           String typeName1, String typeName2, String termName, String termValue) {

        //search result get source
        SearchResponse sResponse = client.prepareSearch(indexName1, indexName2)
                .setTypes(typeName1, typeName2)
                //设置search type
                //常用search type用：query_then_fetch
                //query_then_fetch是先查到相关结构，然后聚合不同node上的结果后排序
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                //查询的termName和termvalue
                .setQuery(QueryBuilders.termQuery(termName, termValue))
                //设置排序field
//                .addSort(sortField, SortOrder.DESC)
                //设置高亮field
//                .addHighlightedField(highlightField)
                //设置分页
                .setFrom(0).setSize(60)
                .execute()
                .actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
//        System.out.println(tShards+","+timeCost+","+sShards);
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            System.out.println("==================================");
            SearchHit hit = hitArray[i];
            Map<String, Object> fields = hit.getSource();
            for (String key : fields.keySet()) {
                System.out.println(key);
                System.out.println(fields.get(key));
            }
        }


    }

    private static void searchWithBooleanFilter(String indexName, String typeName, String termName1, String termValue1,
                                                String termName2, String termValue2, String sortField, String highlightField) {
        //构建boolean query
        BoolQueryBuilder bq = boolQuery()
                .filter(termQuery(termName1, termValue1))
                .filter(termQuery(termName2, termValue2));
        //.mustNot(termQuery("content", "test2"))
        //.should(termQuery("content", "test3"));
        System.out.println(bq.toString());

        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                //设置search type
                //常用search type用：query_then_fetch
                //query_then_fetch是先查到相关结构，然后聚合不同node上的结果后排序
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                //查询的termName和termvalue
                .setQuery(bq)
                //设置排序field
                .addSort(sortField, SortOrder.DESC)
                //设置高亮field
                .addHighlightedField(highlightField)
                //设置分页
                .setFrom(0).setSize(60)
                .execute()
                .actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
        System.out.println(tShards + "," + timeCost + "," + sShards);
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            SearchHit hit = hitArray[i];
            Map<String, Object> fields = hit.getSource();
            for (String key : fields.keySet()) {
                System.out.println(key);
                System.out.println(fields.get(key));
            }
        }

    }

    private static void searchWithFilter(String indexName, String typeName,
                                         String termName, String termValue, String sortField,
                                         String highlightField) {
        // search result get source
        ConstantScoreQueryBuilder csq = QueryBuilders.constantScoreQuery(QueryBuilders.termQuery(termName, termValue));
        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(csq)
                .setFrom(0).setSize(60).execute().actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
        // System.out.println(tShards+","+timeCost+","+sShards);
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            System.out.println("==================================");
            SearchHit hit = hitArray[i];
            Map<String, Object> fields = hit.getSource();
            for (String key : fields.keySet()) {
                System.out.println(key);
                System.out.println(fields.get(key));
            }
        }
    }

    /**
     * 用docId获取document
     *
     * @param indexName
     * @param typeName
     * @param docId
     */
    private static void getWithId(String indexName, String typeName, String docId) {
        //get with id
        GetResponse gResponse = client.prepareGet(indexName, typeName, docId)
                .execute()
                .actionGet();
        System.out.println(gResponse.getIndex());
        System.out.println(gResponse.getType());
        System.out.println(gResponse.getVersion());
        System.out.println(gResponse.isExists());
        Map<String, Object> results = gResponse.getSource();
        if (results != null) {
            for (String key : results.keySet()) {
                Object field = results.get(key);
                System.out.println(key);
                System.out.println(field);
            }
        }
    }

    private static void multiSearch() {
        // TODO Auto-generated method stub

    }

    private static void indexWithBulk(String index, String type) {
        //指定索引名称，type名称和documentId(documentId可选，不设置则系统自动生成)创建document
        IndexRequest ir1 = new IndexRequest();
        String source1 = "{" +
                "\"user\":\"kimchy\"," +
                "\"price\":\"6.3\"," +
                "\"tid\":\"20001\"," +
                "\"message\":\"Elasticsearch\"" +
                "}";
        ir1.index(index).type(type).id("100").source(source1);
        IndexRequest ir2 = new IndexRequest();
        String source2 = "{" +
                "\"user\":\"kimchy2\"," +
                "\"price\":\"7.3\"," +
                "\"tid\":\"20002\"," +
                "\"message\":\"Elasticsearch\"" +
                "}";
        ir2.index(index).type(type).id("102").source(source2);
        IndexRequest ir3 = new IndexRequest();
        String source3 = "{" +
                "\"user\":\"kimchy3\"," +
                "\"price\":\"8.3\"," +
                "\"tid\":\"20003\"," +
                "\"message\":\"Elasticsearch\"" +
                "}";
        ir3.index(index).type(type).id("103").source(source3);
        BulkResponse response = client.prepareBulk().add(ir1).add(ir2).add(ir3).execute().actionGet();
        BulkItemResponse[] responses = response.getItems();
        if (responses != null && responses.length > 0) {
            for (BulkItemResponse r : responses) {
                String i = r.getIndex();
                String t = r.getType();
                System.out.println(i + "," + t);
            }
        }

    }

    private static void sumCountSearch(String indexName, String typeName,
                                       String sumField, String countField, String searchField, String searchValue) {
        SumBuilder sb = AggregationBuilders.sum("sumPrice").field(sumField);
//        StatsBuilder vb = AggregationBuilders.stats("countTid").field(countField);
        TermQueryBuilder tb = QueryBuilders.termQuery(searchField, searchValue);
        //search result get source
        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                .setQuery(tb)
//                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .addAggregation(sb)
//                .addAggregation(vb)
                .execute()
                .actionGet();
        Map<String, Aggregation> aggMap = sResponse.getAggregations().asMap();
        if (aggMap != null && aggMap.size() > 0) {
            for (String key : aggMap.keySet()) {
                if ("sumPrice".equals(key)) {
                    Sum s = (Sum) aggMap.get(key);
                    System.out.println(key + "," + s.getValue());
                } else if ("countTid".equals(key)) {
                    StatsBuilder c = (StatsBuilder) aggMap.get(key);
                    System.out.println(key + "," + c.toString());
                }

            }
        }
    }

    private static void updateDoc(String indexName, String typeName, String id) throws IOException, InterruptedException, ExecutionException {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(indexName);
        updateRequest.type(typeName);
        updateRequest.id(id);
        updateRequest.doc(jsonBuilder()
                .startObject()
                .field("gender", "male")
                .endObject());
        UpdateResponse resp = client.update(updateRequest).get();
        resp.getClass();
    }


    private static void scrollSearch(String indexName, String typeName,
                                     String... ids) {
        IdsQueryBuilder qb = QueryBuilders.idsQuery().addIds(ids);
        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                .setSearchType(SearchType.SCAN)
                .setQuery(qb)
                .setScroll(new TimeValue(100))
                .setSize(50)
                .execute()
                .actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
        System.out.println(tShards + "," + timeCost + "," + sShards);

        while (true) {
            SearchHits hits = sResponse.getHits();
            SearchHit[] hitArray = hits.getHits();
            for (int i = 0; i < hitArray.length; i++) {
                SearchHit hit = hitArray[i];
                Map<String, Object> fields = hit.getSource();
                for (String key : fields.keySet()) {
                    System.out.println(key);
                    System.out.println(fields.get(key));
                }
            }
            sResponse = client.prepareSearchScroll(sResponse.getScrollId()).setScroll(new TimeValue(100)).execute().actionGet();
            //Break condition: No hits are returned
            if (sResponse.getHits().getHits().length == 0) {
                break;
            }
        }
    }

    private static void deleteDocuments(String string, String string2) {
        SearchResponse sResponse = client.prepareSearch(string)
                .setTypes(string2)
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchAllQuery())
                .setFrom(0).setSize(60)
                .execute()
                .actionGet();
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        List<String> ids = new ArrayList<String>(hitArray.length);
        for (int i = 0; i < count; i++) {
            System.out.println("==================================");
            SearchHit hit = hitArray[i];
            ids.add(hit.getId());

        }
        for (String id : ids) {
            DeleteResponse response = client.prepareDelete(string, string2, id)
                    .execute()
                    .actionGet();
        }

    }

    private static void dateRangeSearch(String indexName, String typeName,
                                        String termName, String from, String to) {
        // 构建range query
        //2015-08-20 12:27:11
        QueryBuilder qb = QueryBuilders.rangeQuery(termName).from(from).to(to);
        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                // 设置search type
                // 常用search type用：query_then_fetch
                // query_then_fetch是先查到相关结构，然后聚合不同node上的结果后排序
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                // 查询的termName和termvalue
                .setQuery(qb)
                // 设置排序field
                .addSort(termName, SortOrder.DESC)
                // 设置分页
                .setFrom(0).setSize(60).execute().actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
        System.out.println(tShards + "," + timeCost + "," + sShards);
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            SearchHit hit = hitArray[i];
            Map<String, Object> fields = hit.getSource();
            for (String key : fields.keySet()) {
                System.out.println(key);
                System.out.println(fields.get(key));
            }
        }

    }

    private static void dateRangeSearch2(String indexName, String typeName,
                                         String termName, String from, String to) {
        // 构建range query
        //2015-08-20 12:27:11
//                DateTimeFormatter formatter = ISODateTimeFormat.basicDateTime();
//                LocalDate fromDateTime = formatter.parseLocalDate(from);
//                LocalDate toDateTime = formatter.parseLocalDate(to);
        QueryBuilder qb = QueryBuilders.rangeQuery(termName).from(from).to(to);
        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                // 设置search type
                // 常用search type用：query_then_fetch
                // query_then_fetch是先查到相关结构，然后聚合不同node上的结果后排序
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                // 查询的termName和termvalue
                .setQuery(qb)
                // 设置排序field
                .addSort(termName, SortOrder.DESC)
                // 设置分页
                .setFrom(0).setSize(60).execute().actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
        System.out.println(tShards + "," + timeCost + "," + sShards);
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            SearchHit hit = hitArray[i];
            Map<String, Object> fields = hit.getSource();
            for (String key : fields.keySet()) {
                System.out.println(key);
                System.out.println(fields.get(key));
            }
        }

    }

    private static void countWithQuery(String indexName, String typeName, String termName, String termValue, String sortField, String highlightField) {
        //search result get source
        CountResponse cResponse = client.prepareCount(indexName)
                .setTypes(typeName)
                .setQuery(QueryBuilders.termQuery(termName, termValue))
                .execute()
                .actionGet();
        int tShards = cResponse.getTotalShards();
        int sShards = cResponse.getSuccessfulShards();
        System.out.println(tShards + "," + sShards);
        long count = cResponse.getCount();

    }

    private static void rangeSearchWithOtherSearch(String indexName, String typeName,
                                                   String termName, String min, String max, String termQueryField) {
        // 构建range query
        QueryBuilder qb = QueryBuilders.rangeQuery(termName).from(min).to(max);
        TermQueryBuilder tb = QueryBuilders.termQuery(termName, termQueryField);
        BoolQueryBuilder bq = boolQuery().must(qb).must(tb);
        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                // 设置search type
                // 常用search type用：query_then_fetch
                // query_then_fetch是先查到相关结构，然后聚合不同node上的结果后排序
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                // 查询的termName和termvalue
                .setQuery(bq)
                // 设置排序field
                .addSort(termName, SortOrder.DESC)
                // 设置分页
                .setFrom(0).setSize(60).execute().actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
        System.out.println(tShards + "," + timeCost + "," + sShards);
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            SearchHit hit = hitArray[i];
            Map<String, Object> fields = hit.getSource();
            for (String key : fields.keySet()) {
                System.out.println(key);
                System.out.println(fields.get(key));
            }
        }


    }

    private static void termRangeSearch(String indexName, String typeName,
                                        String termName, String min, String max, String highlightField) {

        // 构建range query
        QueryBuilder qb = QueryBuilders.rangeQuery(termName).from(min).to(max);
        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                // 设置search type
                // 常用search type用：query_then_fetch
                // query_then_fetch是先查到相关结构，然后聚合不同node上的结果后排序
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                // 查询的termName和termvalue
                .setQuery(qb)
                // 设置排序field
                .addSort(termName, SortOrder.DESC)
                //设置高亮field
                .addHighlightedField(highlightField)
                // 设置分页
                .setFrom(0).setSize(60).execute().actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
        System.out.println(tShards + "," + timeCost + "," + sShards);
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            SearchHit hit = hitArray[i];
            Map<String, Object> fields = hit.getSource();
            for (String key : fields.keySet()) {
                System.out.println(key);
                System.out.println(fields.get(key));
            }
        }


    }

    private static void sumOneField(String indexName, String typeName, String fieldName) {
        SumBuilder sb = AggregationBuilders.sum("sum").field(fieldName);
        //search result get source
        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                .addAggregation(sb)
                .execute()
                .actionGet();
        Map<String, Aggregation> aggMap = sResponse.getAggregations().asMap();
        if (aggMap != null && aggMap.size() > 0) {
            for (String key : aggMap.keySet()) {
                Sum s = (Sum) aggMap.get(key);
                System.out.println(s.getValue());
            }
        }
    }

    private static void searchWithTermQueryAndRetureSpecifiedFields(String indexName, String typeName, String termName,
                                                                    String termValue, String sortField, String highlightField,
                                                                    String... fields) {
        // search result get specified fields
        SearchRequestBuilder sb = client.prepareSearch(indexName)
                .setTypes(typeName)
                // 设置search type
                // 常用search type用：query_then_fetch
                // query_then_fetch是先查到相关结构，然后聚合不同node上的结果后排序
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                // 查询的termName和termvalue
                .setQuery(QueryBuilders.termQuery(termName, termValue))
                // 设置排序field
                .addSort(sortField, SortOrder.DESC)
                // 设置高亮field
                .addHighlightedField(highlightField)
                // 设置分页
                .setFrom(0).setSize(60);
        for (String field : fields) {
            sb.addField(field);
        }
        SearchResponse sResponse = sb.execute().actionGet();
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            SearchHit hit = hitArray[i];
            Map<String, SearchHitField> fm = hit.getFields();
            for (String key : fm.keySet()) {
                SearchHitField f = fm.get(key);
                System.out.println(f.getName());
                System.out.println(f.getValue().toString());
            }
        }
    }

    private static void searchWithIds(String indexName, String typeName, String sortField, String highlightField,
                                      String... ids) {
        IdsQueryBuilder qb = QueryBuilders.idsQuery().addIds(ids);
        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                //设置search type
                //常用search type用：query_then_fetch
                //query_then_fetch是先查到相关结构，然后聚合不同node上的结果后排序
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                //查询的termName和termvalue
                .setQuery(qb)
                //设置排序field
                .addSort(sortField, SortOrder.DESC)
                //设置高亮field
                .addHighlightedField(highlightField)
                //设置分页
                .setFrom(0).setSize(60)
                .execute()
                .actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
        System.out.println(tShards + "," + timeCost + "," + sShards);
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            SearchHit hit = hitArray[i];
            Map<String, Object> fields = hit.getSource();
            for (String key : fields.keySet()) {
                System.out.println(key);
                System.out.println(fields.get(key));
            }
        }

    }

    /**
     * 在index:indexName, type:typeName中做通配符查询
     *
     * @param indexName
     * @param typeName
     * @param termName
     * @param termValue
     * @param sortField
     * @param highlightField
     */
    private static void wildcardSearch(String indexName, String typeName, String termName, String termValue, String sortField, String highlightField) {
        QueryBuilder qb = QueryBuilders.wildcardQuery(termName, termValue);
        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                //设置search type
                //常用search type用：query_then_fetch
                //query_then_fetch是先查到相关结构，然后聚合不同node上的结果后排序
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                //查询的termName和termvalue
                .setQuery(qb)
                //设置排序field
//                .addSort(sortField, SortOrder.DESC)
                //设置高亮field
//                .addHighlightedField(highlightField)
                //设置分页
                .setFrom(0).setSize(60)
                .execute()
                .actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
        System.out.println(tShards + "," + timeCost + "," + sShards);
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            SearchHit hit = hitArray[i];
            Map<String, Object> fields = hit.getSource();
            for (String key : fields.keySet()) {
                System.out.println(key);
                System.out.println(fields.get(key));
            }
        }

    }

    /**
     * 在index:indexName, type:typeName中做模糊查询
     *
     * @param indexName
     * @param typeName
     * @param termName
     * @param termValue
     * @param sortField
     * @param highlightField
     */
    private static void fuzzySearch(String indexName, String typeName, String termName, String termValue, String sortField, String highlightField) {
        QueryBuilder qb = QueryBuilders.fuzzyQuery(termName, termValue);
        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                //设置search type
                //常用search type用：query_then_fetch
                //query_then_fetch是先查到相关结构，然后聚合不同node上的结果后排序
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                //查询的termName和termvalue
                .setQuery(qb)
                //设置排序field
                .addSort(sortField, SortOrder.DESC)
                //设置高亮field
                .addHighlightedField(highlightField)
                //设置分页
                .setFrom(0).setSize(60)
                .execute()
                .actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
        System.out.println(tShards + "," + timeCost + "," + sShards);
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            SearchHit hit = hitArray[i];
            Map<String, Object> fields = hit.getSource();
            for (String key : fields.keySet()) {
                System.out.println(key);
                System.out.println(fields.get(key));
            }
        }

    }


    /**
     * 在index:indexName, type:typeName中做区间查询
     *
     * @param indexName
     * @param typeName
     * @param termName
     * @param min
     * @param max
     * @param highlightField
     */
    private static void numericRangeSearch(String indexName, String typeName,
                                           String termName, double min, double max, String highlightField) {
        // 构建range query
        QueryBuilder qb = QueryBuilders.rangeQuery(termName).from(min).to(max);
        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                // 设置search type
                // 常用search type用：query_then_fetch
                // query_then_fetch是先查到相关结构，然后聚合不同node上的结果后排序
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                // 查询的termName和termvalue
                .setQuery(qb)
                // 设置排序field
                .addSort(termName, SortOrder.DESC)
                //设置高亮field
                .addHighlightedField(highlightField)
                // 设置分页
                .setFrom(0).setSize(60).execute().actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
        System.out.println(tShards + "," + timeCost + "," + sShards);
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            SearchHit hit = hitArray[i];
            Map<String, Object> fields = hit.getSource();
            for (String key : fields.keySet()) {
                System.out.println(key);
                System.out.println(fields.get(key));
            }
        }

    }


    /**
     * 在索引indexName, type为typeName中查找两个term：term1(termName1, termValue1)和term2(termName2, termValue2)
     *
     * @param indexName
     * @param typeName
     * @param termName1
     * @param termValue1
     * @param termName2
     * @param termValue2
     * @param sortField
     * @param highlightField
     */
    private static void searchWithBooleanQuery(String indexName, String typeName, String termName1, String termValue1,
                                               String termName2, String termValue2, String sortField, String highlightField) {
        //构建boolean query
        BoolQueryBuilder bq = boolQuery()
                .must(termQuery(termName1, termValue1))
                .must(termQuery(termName2, termValue2));
        //.mustNot(termQuery("content", "test2")) 
        //.should(termQuery("content", "test3"));
        System.out.println(bq.toString());

        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                //设置search type
                //常用search type用：query_then_fetch
                //query_then_fetch是先查到相关结构，然后聚合不同node上的结果后排序
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                //查询的termName和termvalue
                .setQuery(bq)
                //设置排序field
                .addSort(sortField, SortOrder.DESC)
                //设置高亮field
                .addHighlightedField(highlightField)
                //设置分页
                .setFrom(0).setSize(60)
                .execute()
                .actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
        System.out.println(tShards + "," + timeCost + "," + sShards);
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            SearchHit hit = hitArray[i];
            Map<String, Object> fields = hit.getSource();
            for (String key : fields.keySet()) {
                System.out.println(key);
                System.out.println(fields.get(key));
            }
        }
    }


    /**
     * 在索引indexName, type为typeName中查找term(termName, termValue)
     *
     * @param indexName
     * @param typeName
     * @param termName
     * @param termValue
     * @param sortField
     * @param highlightField
     */
    private static void searchWithTermQuery(String indexName, String typeName, String termName, String termValue, String sortField, String highlightField) {
        //search result get source
        SearchResponse sResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                //设置search type
                //常用search type用：query_then_fetch
                //query_then_fetch是先查到相关结构，然后聚合不同node上的结果后排序
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                //查询的termName和termvalue
                .setQuery(QueryBuilders.termQuery(termName, termValue))
                //设置排序field
//                .addSort(sortField, SortOrder.DESC)
                //设置高亮field
//                .addHighlightedField(highlightField)
                //设置分页
                .setFrom(0).setSize(60)
                .execute()
                .actionGet();
        int tShards = sResponse.getTotalShards();
        long timeCost = sResponse.getTookInMillis();
        int sShards = sResponse.getSuccessfulShards();
//        System.out.println(tShards+","+timeCost+","+sShards);
        SearchHits hits = sResponse.getHits();
        long count = hits.getTotalHits();
        SearchHit[] hitArray = hits.getHits();
        for (int i = 0; i < count; i++) {
            System.out.println("==================================");
            SearchHit hit = hitArray[i];
            Map<String, Object> fields = hit.getSource();
            for (String key : fields.keySet()) {
                System.out.println(key);
                System.out.println(fields.get(key));
            }
        }
    }


    /**
     * 用java的map构建document
     */
    private static void indexWithMap(String indexName, String typeName) {
        Map<String, Object> json = new HashMap<String, Object>();
        //设置document的field
        json.put("user", "kimchy2");
        json.put("postDate", new Date());
        json.put("price", 6.4);
        json.put("message", "Elasticsearch");
        json.put("tid", "10002");
//        json.put("location","-77.03653, 38.897676");
        json.put("endTime", "2015-08-25 09:00:00");
        //指定索引名称，type名称和documentId(documentId可选，不设置则系统自动生成)创建document
        IndexResponse response = client.prepareIndex(indexName, typeName, "2")
                .setSource(json)
                .execute()
                .actionGet();
        //response中返回索引名称，type名称，doc的Id和版本信息
        String index = response.getIndex();
        String type = response.getType();
        String id = response.getId();
        long version = response.getVersion();
        boolean created = response.isCreated();
        System.out.println(index + "," + type + "," + id + "," + version + "," + created);
    }

    /**
     * 用java字符串创建document
     */
    private static void indexWithStr(String indexName, String typeName) {
        //手工构建json字符串
        //该document包含user, postData和message三个field
        String json = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"price\":\"6.3\"," +
                "\"tid\":\"10001\"," +
//                "\"location\":\"-77.03653, 38.897676\"," +
                "\"endTime\":\"2015-08-19 09:00:00\"," +
                "\"message\":\"Elasticsearch\"" +
                "}";
        //指定索引名称，type名称和documentId(documentId可选，不设置则系统自动生成)创建document
        IndexResponse response = client.prepareIndex(indexName, typeName, "1")
                .setSource(json)
                .execute()
                .actionGet();
        //response中返回索引名称，type名称，doc的Id和版本信息
        String index = response.getIndex();
        String type = response.getType();
        String id = response.getId();
        long version = response.getVersion();
        boolean created = response.isCreated();
        System.out.println(index + "," + type + "," + id + "," + version + "," + created);
    }

    /**
     * 创建es client 一定要是单例，单例，单例！不要在应用中构造多个客户端！
     * clusterName:集群名字
     * nodeIp:集群中节点的ip地址
     * nodePort:节点的端口
     *
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
                .addTransportAddress(
                        new InetSocketTransportAddress(InetAddress.getByName(nodeIp),
                                nodePort));

        return client;
    }


    private static void deleteDocWithId(String indexName, String typeName, String docId) {
        DeleteResponse dResponse = client.prepareDelete(indexName, typeName, docId)
                .execute()
                .actionGet();
        String index = dResponse.getIndex();
        String type = dResponse.getType();
        String id = dResponse.getId();
        long version = dResponse.getVersion();
        System.out.println(index + "," + type + "," + id + "," + version);
    }

    /**
     * 创建索引
     * 注意：在生产环节中通知es集群的owner去创建index
     *
     * @param indexName
     * @param typeName
     * @throws IOException
     */
    private static void createIndex(String indexName, String typeName) throws IOException {
        final IndicesExistsResponse iRes = client.admin().indices().prepareExists(indexName).execute().actionGet();
        if (iRes.isExists()) {
            client.admin().indices().prepareDelete(indexName).execute().actionGet();
        }
        client.admin().indices().prepareCreate(indexName).setSettings(Settings.settingsBuilder().put("number_of_shards", 1).put("number_of_replicas", "0")).execute().actionGet();
        XContentBuilder mapping = jsonBuilder()
                .startObject()
                .startObject(typeName)
//                     .startObject("_routing").field("path","tid").field("required", "true").endObject()
                .startObject("_source").field("enabled", "true").endObject()
                .startObject("_all").field("enabled", "false").endObject()
                .startObject("properties")
                .startObject("user")
                .field("store", true)
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()
                .startObject("message")
                .field("store", true)
                .field("type", "string")
                .field("index", "analyzed")
                .field("analyzer", "standard")
                .endObject()
                .startObject("price")
                .field("store", true)
                .field("type", "float")
                .endObject()
                .startObject("nv1")
                .field("store", true)
                .field("type", "integer")
                .field("index", "no")
                .field("null_value", 0)
                .endObject()
                .startObject("nv2")
                .field("store", true)
                .field("type", "integer")
                .field("index", "not_analyzed")
                .field("null_value", 10)
                .endObject()
                .startObject("tid")
                .field("store", true)
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()
//                               .startObject("location")
//                                    .field("store", true)
//                                  .field("type", "geo_point")
//                                  .field("lat_lon", true)
//                                  .field("geohash", true)
//                                  .field("geohash_prefix", true)
//                                  .field("geohash_precision", 7)
//                               .endObject()
//                               .startObject("shape")
//                                    .field("store", true)
//                                  .field("type", "geo_shape")
//                                  .field("geohash", true)
//                                  .field("geohash_prefix", false)
//                                  .field("geohash_precision", 7)
//                               .endObject()
                .startObject("endTime")
                .field("type", "date")
                .field("store", true)
                .field("index", "not_analyzed")
                //2015-08-21T08:35:13.890Z
                .field("format", "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .endObject()
                .startObject("date")
                .field("type", "date")
//                                  .field("store", true)
//                                  .field("index", "not_analyzed")
                //2015-08-21T08:35:13.890Z
//                                  .field("format", "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .endObject()
                .endObject()
                .endObject()
                .endObject();
        client.admin().indices()
                .preparePutMapping(indexName)
                .setType(typeName)
                .setSource(mapping)
                .execute().actionGet();
    }


}