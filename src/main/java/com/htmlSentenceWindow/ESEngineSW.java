package com.htmlSentenceWindow;

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
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//todo: add search operation
public class ESEngineSW {

    String index = "ibm3s";

    private Logger logger = LogManager.getLogger(ESSetter.class);

    public ESEngineSW withIndex(String newIndex) {
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
    GET ibm3s/_search
{
  "from" : 0, "size" : 10,
    "query": {
        "query_string" : {
            "default_field" : "sentence",
            "query" : "(audit_log_file) AND (is_specified)",
            "analyzer" : "my_entity_analyzer",
            "minimum_should_match" : "10%"
        }
    },
    "sort" : [
        "_score"
    ]
}
     */

    public List<ResultHitJsonSW> searchEntitiesMatchString(List<String> entitys) {
        try {
            List<ResultHitJsonSW> results = new ArrayList<>();
            TransportClient client = getClient();
            String queryString = "";
            for (String e : entitys) {
                queryString = queryString + " AND " + "(" + e + ") ";
            }

            queryString = queryString.replaceFirst(" AND ", "").trim();

            SearchResponse response = client.prepareSearch(index)
                    .setTypes("document")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.queryStringQuery(queryString).field("sentence").analyzer("my_entity_analyzer"))
                    .setFrom(0)
                    .setSize(10)
                    .addSort("_score", SortOrder.DESC)
                    .execute().actionGet();

            for (SearchHit hit : response.getHits()) {
                Map map = hit.getSourceAsMap();
                ResultHitJsonSW r = new ResultHitJsonSW(map, entitys);
                results.add(r);
            }

            client.close();
            return results;

        } catch (Exception e) {
            logger.error("failed to put search. ", e);
            return null;
        }
    }

       /*"query" : {
      "bool": {
          "must": [
            {"match_phrase" : {
                "sentence" : {"query" : "audit_log_file",
                "analyzer" : "my_entity_analyzer"}
              }},
              {"match_phrase" : {
                "sentence" : {
                "query" : "is_specified",
                "analyzer" : "my_entity_analyzer"}
              }}
          ]
      }
  }*/
    public List<ResultHitJsonSW> searchEntitiesMatchPhrase(List<String> entitys) {
        try {
            List<ResultHitJsonSW> results = new ArrayList<>();
            TransportClient client = getClient();

            String entityAnalyzer = "my_entity_analyzer";
            String default_field = "sentence";

            BoolQueryBuilder qb = QueryBuilders.boolQuery();
            for (String e : entitys) {
                qb = qb.must(QueryBuilders.matchPhraseQuery(default_field, e).analyzer(entityAnalyzer));
            }

            SearchResponse response = client.prepareSearch(index)
                    .setTypes("document")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(qb)
                    .execute().actionGet();

            for (SearchHit hit : response.getHits()) {
                Map map = hit.getSourceAsMap();
                ResultHitJsonSW r = new ResultHitJsonSW(map, entitys);
                results.add(r);
            }

            client.close();
            return results;

        } catch (Exception e) {
            logger.error("failed to put search. ", e);
            return null;
        }
    }

    public void saveResult(List<ResultHitJsonSW> result, String resultIndex) {
        try {
            List<String> jsonStrs = new ArrayList();
            ObjectMapper mapper = new ObjectMapper();
            for (ResultHitJsonSW r : result) {
                jsonStrs.add(mapper.writeValueAsString(r));
            }

            ESSetter esSetter = new ESSetter(resultIndex);
            esSetter.putDocBulk(jsonStrs);
        } catch (Exception e) {
            logger.error("failed to load sample data: ", e);
        }

    }

}
