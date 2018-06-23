//package com.esutil;
//
//import com.ValueTypes.EntityMatch;
//import com.ValueTypes.Sentence;
//import com.ValueTypes.SentenseWindow;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.lucene.search.join.ScoreMode;
//import org.elasticsearch.action.get.GetResponse;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.search.SearchType;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.TransportAddress;
//import org.elasticsearch.index.query.InnerHitBuilder;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
////todo: add search operation
//public class ESEngine {
//
//    String index = "ibmnested3";
//
//    private Logger logger = LogManager.getLogger(ESSetter.class);
//
//    public ESEngine withIndex(String newIndex) {
//        this.index = newIndex;
//        return this;
//    }
//
//    public void tryQuery() {
//        try {
//            // on startup
//            TransportClient client = getClient();
//            GetResponse getResponse = client.prepareGet(index, "document", "1").get();
//
//            SearchResponse response = client.prepareSearch(index)
//                    .setTypes("document")
//                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                    .setQuery(QueryBuilders.nestedQuery("content",
//                            QueryBuilders.boolQuery().
//                                    must(QueryBuilders.matchQuery("content.sentence", "parameter")),
//                            ScoreMode.Avg))
//                    .execute().actionGet();
//
//            int count = 0;
//            for (SearchHit hit : response.getHits()) {
//                Map map = hit.getSourceAsMap();
//                if (count < 3) {
//                    System.out.println(map);
//                } else {
//                    break;
//                }
//                ++count;
//            }
//
//            client.close();
//        } catch (Exception e) {
//            logger.error("failed to put search. ", e);
//            return;
//        }
//    }
//
//    public TransportClient getClient() {
//        try {
//            Settings settings = Settings.builder().put("cluster.name", "elasticsearch_sylvia.wang").build();
//            TransportClient client = new PreBuiltTransportClient(settings)
//                    .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
//            return client;
//        } catch (UnknownHostException e) {
//            logger.error("failed to find host. ", e);
//            return null;
//        }
//    }
//
//    public List<EntityMatch> searchEntities(String entity) {
//        try {
//            // on startup
//            TransportClient client = getClient();
//
//            SearchResponse response = client.prepareSearch(index)
//                    .setTypes("document")
//                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                    .setFetchSource("_id", "*")
//                    .setQuery(QueryBuilders.nestedQuery("content",
//                            QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("content.sentence", entity)), ScoreMode.Avg)
//                            .innerHit(new InnerHitBuilder("content")
//                                    .setFetchSourceContext(new FetchSourceContext(true,
//                                            new String[]{"content.sentence", "content.id"}, null))
//                                    .addDocValueField("content.id")
//                                    .setSize(10)))
//                    .execute().actionGet();
//
//            List<EntityMatch> matchHits = new ArrayList<>();
//
//            for (SearchHit hit : response.getHits().getHits()) {
//                if (hit.getInnerHits() != null) {
//                    for (String innerHitKey : hit.getInnerHits().keySet()) {
//                        System.out.println(innerHitKey);
//                        for (SearchHit innerHit : hit.getInnerHits().get(innerHitKey).getHits()) {
//                            EntityMatch oneHit = new EntityMatch();
//                            oneHit.setSentenceId(innerHit.getFields().get("content.id").getValue());
//                            oneHit.setDocId(innerHit.getFields().get("").getValue());
//                            matchHits.add(oneHit);
//                        }
//                    }
//                }
//            }
//            client.close();
//            return matchHits;
//
//        } catch (Exception e) {
//            logger.error("failed to put search. ", e);
//            return null;
//        }
//    }
//
//    public SentenseWindow getSentenceWindow(EntityMatch entityMatch) {
//        try {
//            // on startup
//            TransportClient client = getClient();
//            long sid = entityMatch.getSentenceId();
//            long id1, id2;
//            if (sid == 0) {
//                id1 = 1;
//                id2 = 2;
//            } else {
//                id1 = sid - 1;
//                id2 = sid + 1;
//            }
//
//            SearchResponse response = client.prepareSearch(index)
//                    .setTypes("document")
//                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                    .setFetchSource(null, "*")
//                    .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("_id", entityMatch.getDocId())))
//                    .setQuery(QueryBuilders.nestedQuery("content",
//                            QueryBuilders.boolQuery().must(QueryBuilders.termQuery("content.id", sid)), ScoreMode.Avg)
//                            .innerHit(new InnerHitBuilder("content")
//                                    .setFetchSourceContext(new FetchSourceContext(true,
//                                            new String[]{"content.sentence", "content.id"}, null))
//                                    .addDocValueField("content.id")
//                                    .setSize(1)))
//                    .execute().actionGet();
//
//           SentenseWindow matchHit = new SentenseWindow();
//           List<Sentence>  sentences = new ArrayList<>();
//
//            for (SearchHit hit : response.getHits().getHits()) {
//                if (hit.getInnerHits() != null) {
//                    for (String innerHitKey : hit.getInnerHits().keySet()) {
//                        System.out.println(innerHitKey);
//                        for (SearchHit innerHit : hit.getInnerHits().get(innerHitKey).getHits()) {
//                            Sentence s = new Sentence();
//                            s.setId(innerHit.getFields().get("content.id").getValue());
//                            s.setSentence(innerHit.getFields().get("").getValue());
//                            sentences.add(s);
//                        }
//                    }
//                    matchHit.setDocId("");
//                    matchHit.setEntity("");
//                }
//
//            }
//            client.close();
//            return matchHit;
//
//        } catch (Exception e) {
//            logger.error("failed to put search. ", e);
//            return null;
//        }
//    }
//
//    public void searchSentenseWindow(String entity) {
//        List<EntityMatch> matches1 = searchEntities(entity);
//        List<SentenseWindow> matches2 = new ArrayList<>();
//        for (EntityMatch em : matches1) {
//            matches2.add(getSentenceWindow(em));
//        }
//
//        //send to ES
//
//    }
//
//
//}
