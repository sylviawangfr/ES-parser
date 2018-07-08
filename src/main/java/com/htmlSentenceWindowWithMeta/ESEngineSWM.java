package com.htmlSentenceWindowWithMeta;

import com.esutil.ESSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//todo: add search operation
public class ESEngineSWM {

    String index = "ibm3s-with-meta";
    String resultIndex = "ibm3s-with-meta-result";

    private Logger logger = LogManager.getLogger(ESSetter.class);

    public ESEngineSWM withIndex(String newIndex) {
        this.index = newIndex;
        return this;
    }

    public void tryQuery() {
        try {
            // on startup
            TransportClient client = getClient();
            //GetResponse getResponse = client.prepareGet(index, "document", "1").get();

            String queryString = "+storwize_error_code_1145 +has_url +it_kg_version1#storwize_error_code_1145_url";
            String queryString2 = "+parameter +device +supply";
            SearchResponse response = client.prepareSearch(index)
                    .setTypes("document")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.simpleQueryStringQuery(queryString2).field("sentence"))
                    .execute().actionGet();

            int count = 0;
            for (SearchHit hit : response.getHits()) {
                Map map = hit.getSourceAsMap();
                if (count < 3) {
                    System.out.println(map);
                } else {
                    break;
                }
                ++count;
            }

            client.close();
        } catch (Exception e) {
            logger.error("failed to put search. ", e);
            return;
        }
    }

    public TransportClient getClient() {
        try {
            Settings settings = Settings.builder().put("cluster.name", "elasticsearch_sylvia.wang").build();
            TransportClient client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
            return client;
        } catch (UnknownHostException e) {
            logger.error("failed to find host. ", e);
            return null;
        }
    }
/*
    GET ibm3s-with-meta/_search
    {
        "from" : 0, "size" : 5,
            "query": {
        "bool": {
            "should": [
            {"multi_match" : {
                "query" : "storwize_error_code_CMMVC5743E",
                        "fields" : ["sentence"],
                "analyzer" : "my_entity_analyzer",
                        "boost": 5
            }
            },
            {"more_like_this" : {
                "fields" : ["title", "sentence", "description"],
                "like" : "has_url",
                        "min_term_freq" : 1,
                        "max_query_terms" : 12,
                        "analyzer" : "type"
            }},
            {"more_like_this" : {
                "fields" : ["title", "sentence", "description"],
                "like" : "storwize_error_code_CMMVC5743E_url",
                        "min_term_freq" : 1,
                        "max_query_terms" : 12,
                        "analyzer" : "my_entity_analyzer",
                        "boost": 1
            }}
        ],
            "minimum_should_match" : "50%"
        }
    },
        "sort" : "_score"
    }
*/
    public List<ResultHitJsonSWM> searchEntitiesMixQuery(List<String> entities) {
        try {
            List<ResultHitJsonSWM> results = new ArrayList<>();
            TransportClient client = getClient();

            String entityAnalyzer = "my_entity_analyzer";
            String default_field = "sentence";

            BoolQueryBuilder qb = QueryBuilders.boolQuery();
            if (StringUtils.hasText(entities.get(0))) {
                qb = qb.should(QueryBuilders.matchQuery(default_field, entities.get(0)).analyzer(entityAnalyzer).boost(5f));
            }

            if (StringUtils.hasText(entities.get(1))) {
                qb = qb.should(QueryBuilders.moreLikeThisQuery(new String[]{entities.get(1)}).analyzer(entityAnalyzer).minTermFreq(1).maxQueryTerms(3));
            }

            if (StringUtils.hasText(entities.get(2))) {
                qb = qb.should(QueryBuilders.moreLikeThisQuery(new String[]{entities.get(2)}).analyzer(entityAnalyzer).minTermFreq(1).maxQueryTerms(3));
            }
            qb = qb.minimumShouldMatch("50%");

            SearchResponse response = client.prepareSearch(index)
                    .setTypes("document")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setFrom(0)
                    .setSize(10)
                    .setQuery(qb)
                    .execute().actionGet();

            for (SearchHit hit : response.getHits()) {
                Map map = hit.getSourceAsMap();
                ResultHitJsonSWM r = new ResultHitJsonSWM(map, entities, hit.getScore());
                results.add(r);
            }

            client.close();
            return results;

        } catch (Exception e) {
            logger.error("failed to put search. ", e);
            return null;
        }
    }

    public void saveResult(List<ResultHitJsonSWM> result) {
        try {
            List<String> jsonStrs = new ArrayList();
            ObjectMapper mapper = new ObjectMapper();
            for (ResultHitJsonSWM r : result) {
                jsonStrs.add(mapper.writeValueAsString(r));
            }

            ESSetter esSetter = new ESSetter(resultIndex);
            esSetter.putDocBulk(jsonStrs);
        } catch (Exception e) {
            logger.error("failed to load sample data: ", e);
        }

    }

}
