package com.esutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

//todo: add search operation
public class ESEngine {

    String index;

    private Logger logger = LogManager.getLogger(ESSetter.class);

    public ESEngine withIndex(String newIndex) {
        this.index = newIndex;
        return this;
    }

    public void tryQuery() {
        try {
            // on startup
            TransportClient client = getClient();
            GetResponse getResponse = client.prepareGet("ibm5", "document", "1").get();

            SearchResponse response = client.prepareSearch("ibm5")
                    .setTypes("document")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.matchQuery("body", "ibm"))
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
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
            return client;
        } catch (UnknownHostException e) {
            logger.error("failed to find host. ", e);
            return null;
        }
    }

    public void searchEntities(String entity) {
        try {
            // on startup
            TransportClient client = getClient();

            SearchResponse response = client.prepareSearch(index)
                    .setTypes("document")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setFetchSource(null, "*")
                    .setQuery(QueryBuilders.nestedQuery("content",
                            QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("content.sentence", entity)), ScoreMode.Avg)
                            .innerHit(new InnerHitBuilder("contentInnerHit")
                                    .addDocValueField("content.sentence")
                                    .addDocValueField("content.id")
                                    .setSize(10)))
                    .execute().actionGet();

            for (SearchHit hit : response.getHits().getHits()) {
                if (hit.getInnerHits() != null) {
                    for (String innerHitKey : hit.getInnerHits().keySet()) {
                        System.out.println(innerHitKey);
                        for (SearchHit innerHit : hit.getInnerHits().get(innerHitKey).getHits()) {
                            for (String key : innerHit.getFields().keySet()) {
                                Object innerObj = innerHit.getFields().get(key);
                                //System.out.println(innerObj.getName() + " " + innerObj.getValue());
                            }
                        }
                    }
                }
            }

            for (SearchHit hit : response.getHits()) {
                Map map = hit.getSourceAsMap();

            }

            client.close();
        } catch (Exception e) {
            logger.error("failed to put search. ", e);
            return;
        }
    }


}
