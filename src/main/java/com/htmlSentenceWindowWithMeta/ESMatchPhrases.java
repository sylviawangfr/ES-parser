//package com.htmlSentenceWindowWithMeta;
//
//import com.esutil.ESSetter;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.search.SearchType;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.text.Text;
//import org.elasticsearch.common.transport.TransportAddress;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.reindex.BulkByScrollResponse;
//import org.elasticsearch.index.reindex.DeleteByQueryAction;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
//import org.elasticsearch.search.sort.SortOrder;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//import org.springframework.util.StringUtils;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
////todo: add search operation
//public class ESMatchPhrases {
//
//    String index = "ibm3s-with-meta";
//
//    //TransportClient client = null;
//
//    private Logger logger = LogManager.getLogger(ESMatchPhrases.class);
//
//    public ESMatchPhrases withIndex(String newIndex) {
//        this.index = newIndex;
//        return this;
//    }
//
//    public QueryBuilders addPhraseQuery(BoolQueryBuilder qb, String field, String keyword, String analyzer) {
//        try {
//
//            BoolQueryBuilder qb2 = qb.must(QueryBuilders.matchPhraseQuery(field, keyword).analyzer(analyzer));
//
//            BoolQueryBuilder qb = QueryBuilders.boolQuery()
//                    .must(QueryBuilders.matchQuery("head", entities.get(0)).analyzer(analyzer))
//                    .must(QueryBuilders.matchQuery("label", entities.get(1)).analyzer(analyzer))
//                    .must(QueryBuilders.matchQuery("tail", entities.get(2)).analyzer(analyzer));
//
//
//            SearchResponse response = client.prepareSearch(index)
//                    .setTypes("document")
//                    .setSearchType(SearchType.QUERY_THEN_FETCH)
//                    .setFrom(0)
//                    .setSize(10)
//                    .setQuery(qb)
//                    .addSort("score", SortOrder.DESC)
//                    .execute().actionGet();
//
//            List<String> taggedSW = new ArrayList<>();
//            for (SearchHit hit : response.getHits()) {
//                Map map = hit.getSourceAsMap();
//                ResultHitJsonSWM r = new ResultHitJsonSWM(map, entities, hit.getScore());
//                results.add(r);
//            }
//            return results;
//
//        } catch (Exception e) {
//            logger.error("failed to put find sw by triple. ", e);
//            return null;
//        }
//    }
//
//
//}
