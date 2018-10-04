package com.dbpediaSentenseWindow;

import com.esutil.ESSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.htmlSentenceWindowWithMeta.ResultHitJsonSWM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ESEngineSW {

    String originIndex = "ibm3s-all";
    String tmpTagIndex = "ibm3s-tmp-tag";
    String resultIndex = "ibm3s-tag-result";

    String defaultAnalyzer = "my_analyzer";
    String entityAnalyzer = "my_entity_analyzer";

    //TransportClient client = null;

    private Logger logger = LogManager.getLogger(ESSetter.class);

    public ESEngineSW withIndex(String newIndex) {
        this.originIndex = newIndex;
        return this;
    }

    public ESEngineSW withResultIndex(String newIndex) {
        this.resultIndex = newIndex;
        return this;
    }

    public void tryQuery() {
        try {
            // on startup
            TransportClient client = getClient();
            //GetResponse getResponse = client.prepareGet(index, "document", "1").get();

            String queryString = "+storwize_error_code_1145 +has_url +it_kg_version1#storwize_error_code_1145_url";
            String queryString2 = "+parameter +device +supply";
            SearchResponse response = client.prepareSearch(originIndex)
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
        } catch (Exception e) {
            logger.error("failed to put search. ", e);
            return;
        }
    }

    public TransportClient getClient() {
        try {
//            if (client != null) {
//                return client;
//            }

            Settings settings = Settings.builder().put("cluster.name", "elasticsearch_sylvia.wang").build();
            TransportClient client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
            return client;
        } catch (UnknownHostException e) {
            logger.error("failed to find host. ", e);
            return null;
        }
    }

//    public void closeClient() {
//        if (client != null) {
//            client.close();
//        }
//    }
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

    public List<ResultHitJsonSWM> getSWByEntitiesMixQuery(TransportClient client, List<String> entities) {
        try {
            List<ResultHitJsonSWM> results = new ArrayList<>();

            String default_field = "sentence";

            BoolQueryBuilder qb = QueryBuilders.boolQuery();
            if (StringUtils.hasText(entities.get(0))) {
                qb = qb.should(QueryBuilders.matchQuery(default_field, entities.get(0)).analyzer(entityAnalyzer).boost(2f));
            }

//            if (StringUtils.hasText(entities.get(1))) {
//                qb = qb.should(QueryBuilders.moreLikeThisQuery(new String[]{default_field}, new String[]{entities.get(1)}, null).analyzer(entityAnalyzer).minTermFreq(1).maxQueryTerms(12));
//            }
            String analyzer = defaultAnalyzer;
            if (entities.get(2).contains("_")) {
                analyzer = entityAnalyzer;
            }

            if (StringUtils.hasText(entities.get(2))) {
                qb = qb.should(QueryBuilders.moreLikeThisQuery(new String[]{default_field}, new String[]{entities.get(2)}, null).analyzer(analyzer).minTermFreq(1).maxQueryTerms(12).boost(2f));
            }
            qb = qb.minimumShouldMatch("50%");

            SearchResponse response = client.prepareSearch(originIndex)
                    .setTypes("document")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setFrom(0)
                    .setSize(3)
                    .setQuery(qb)
                    .addSort("_score", SortOrder.DESC)
                    .execute().actionGet();

            for (SearchHit hit : response.getHits()) {
                Map map = hit.getSourceAsMap();
                ResultHitJsonSWM r = new ResultHitJsonSWM(map, entities, hit.getScore());
                results.add(r);
            }
//            if (results.size() > 0) {
//                saveResult(results, tmpResultIndex);
//            }

            return results;

        } catch (Exception e) {
            logger.error("failed to put search. ", e);
            return null;
        }
    }

    public String tagHeadEntity(TransportClient client, ResultHitJsonSWM sw) {
        try {

            String index = originIndex;

            String analyzer = defaultAnalyzer;
            if (sw.head.contains("_")) {
                analyzer = entityAnalyzer;
            }

            BoolQueryBuilder qb = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("fileName", sw.fileName).analyzer(defaultAnalyzer))
                    .must(QueryBuilders.termQuery("number", sw.number))
                    .must(QueryBuilders.matchPhraseQuery("sentence", sw.head).analyzer(analyzer));
            //.must(QueryBuilders.multiMatchQuery(sw.head, "sentence").analyzer(entityAnalyzer));

            //MultiMatchQueryBuilder qb = QueryBuilders.multiMatchQuery(sw.head, "sentence").analyzer(entityAnalyzer);

            HighlightBuilder hb = new HighlightBuilder()
                    .preTags("<head>")
                    .postTags("</head>")
                    .highlighterType("fvh")
                    .boundaryScannerType("sentence")
                    .numOfFragments(0)
                    .fragmentSize(200)
                    .field("sentence");

            SearchResponse response = client.prepareSearch(index)
                    .setTypes("document")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setFrom(0)
                    .setSize(1)
                    .setQuery(qb)
                    .highlighter(hb)
                    .execute().actionGet();

            List<String> taggedSW = new ArrayList<>();
            for (SearchHit hit : response.getHits()) {
                Map<String, HighlightField> esHighlights = hit.getHighlightFields();
                if (!esHighlights.isEmpty()) {
                    for (Map.Entry<String, HighlightField> entry : esHighlights.entrySet()) {
                        for (Text fragment : entry.getValue().getFragments()) {
                            taggedSW.add(fragment.toString());
                        }
                    }
                }

            }


            if (taggedSW.size() > 0) {
                return taggedSW.get(0);
            } else {
                return "";
            }

        } catch (Exception e) {
            logger.error("failed to put search. ", e);
            return null;
        }
    }

    public void sleepMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            System.out.println("interrupted");
        }
    }

    public String tagTailEntity(TransportClient client, ResultHitJsonSWM sw) {
        try {
            String analyzer = "my_analyzer";
            if (sw.tail.contains("_")) {
                analyzer = "my_entity_analyzer";
            }

            //MultiMatchQueryBuilder qb = QueryBuilders.multiMatchQuery(sw.tail, "sentence").analyzer(entityAnalyzer);

            BoolQueryBuilder qb = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("fileName", sw.fileName).analyzer(defaultAnalyzer))
                    .must(QueryBuilders.termQuery("number", sw.number))
                    .must(QueryBuilders.matchPhraseQuery("sentence", sw.tail).analyzer(analyzer));
            //.must(QueryBuilders.multiMatchQuery(sw.tail, "sentence").analyzer(entityAnalyzer));

            HighlightBuilder hb = new HighlightBuilder()
                    .preTags("<tail>")
                    .postTags("</tail>")
                    .highlighterType("fvh")
                    .boundaryScannerType("sentence")
                    .numOfFragments(0)
                    .fragmentSize(200)
                    .field("sentence");

            SearchResponse response = client.prepareSearch(tmpTagIndex)
                    .setTypes("document")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setFrom(0)
                    .setSize(1)
                    .setQuery(qb)
                    .highlighter(hb)
                    .execute().actionGet();

            List<String> taggedSW = new ArrayList<>();
            for (SearchHit hit : response.getHits()) {
                Map<String, HighlightField> esHighlights = hit.getHighlightFields();
                if (!esHighlights.isEmpty()) {
                    for (Map.Entry<String, HighlightField> entry : esHighlights.entrySet()) {
                        for (Text fragment : entry.getValue().getFragments()) {
                            taggedSW.add(fragment.toString());
                        }
                    }
                }
            }
            if (taggedSW.size() > 0) {
                return taggedSW.get(0);
            } else {
                return "";
            }

        } catch (Exception e) {
            logger.error("failed to put search. ", e);
            return null;
        }
    }

    public String mergeTags(String taggedSW) {
        String merged = taggedSW.replace("</head> <head>", " ")
                .replace("</head><head>", "")
                .replace("</tail> <tail>", " ")
                .replace("</tail><tail>", "");

        merged = merged.replaceFirst("<head>", "<HEAD>")
                .replaceFirst("</head>", "</HEAD>")
                .replace("<head>", "")
                .replace("</head>", "")
                .replaceFirst("<tail>", "<TAIL>")
                .replaceFirst("</tail>", "</TAIL>")
                .replace("<tail>", "")
                .replace("</tail>", "")
                .replace("<HEAD>", "<head>")
                .replace("</HEAD>", "</head>")
                .replace("<TAIL>", "<tail>")
                .replace("</TAIL>", "</tail>");


        if (!StringUtils.hasText(merged)) {
            int i = 0;
        }
        return merged;
    }

    public List<ResultHitJsonSWM> getSWByEntitiesMatchQuery(TransportClient client, List<String> entities) {
        try {
            List<ResultHitJsonSWM> results = new ArrayList<>();

            //MultiMatchQueryBuilder qb = QueryBuilders.multiMatchQuery(sw.tail, "sentence").analyzer(entityAnalyzer);

            BoolQueryBuilder qb = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("head", entities.get(0)).analyzer(entityAnalyzer))
                    //.must(QueryBuilders.matchQuery("label", entities.get(1)).analyzer(entityAnalyzer))
                    .must(QueryBuilders.matchQuery("tail", entities.get(2)).analyzer(defaultAnalyzer));


            SearchResponse response = client.prepareSearch(originIndex)
                    .setTypes("document")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setFrom(0)
                    .setSize(3)
                    .setQuery(qb)
                    .addSort("score", SortOrder.DESC)
                    .execute().actionGet();

            List<String> taggedSW = new ArrayList<>();
            for (SearchHit hit : response.getHits()) {
                Map map = hit.getSourceAsMap();
                ResultHitJsonSWM r = new ResultHitJsonSWM(map, entities, hit.getScore());
                results.add(r);
            }
            return results;

        } catch (Exception e) {
            logger.error("failed to put find sw by triple. ", e);
            return null;
        }
    }

    public void searchAndTagAndSave(List<String> entities) {
        TransportClient client = null;
        try {
            client = getClient();
            List<ResultHitJsonSWM> sws = getSWByEntitiesMixQuery(client, entities);

            if (sws == null) {
                sws = getSWByEntitiesMatchQuery(client, entities);
            }

            for (ResultHitJsonSWM sw : sws) {
                ResultHitJsonSWM swTag = new ResultHitJsonSWM(sw);
                String taggedHeadSW = tagHeadEntity(client, sw);
                if (StringUtils.hasText(taggedHeadSW)) {
                    taggedHeadSW = mergeTags(taggedHeadSW);
                    swTag.setSentence(taggedHeadSW);
                    sleepMillis(500);
                    saveTmpTag(swTag);
                    sleepMillis(1000);


                    String taggedTailSW = tagTailEntity(client, swTag);
                    if (StringUtils.hasText(taggedTailSW)) {
                        taggedTailSW = mergeTags(taggedTailSW);
                        swTag.setSentence(taggedTailSW);

                        sleepMillis(500);
                        if (!sw.sentence.equals(swTag.sentence)) {
                            saveResult(swTag, resultIndex);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            client.close();
        }

    }


    public void saveTmpTag(ResultHitJsonSWM sw) {
        try {
            List<String> jsonStrs = new ArrayList();
            ObjectMapper mapper = new ObjectMapper();
            jsonStrs.add(mapper.writeValueAsString(sw));
            ESSetter esSetter = new ESSetter(tmpTagIndex);
            esSetter.putDocBulk(jsonStrs);
        } catch (Exception e) {
            logger.error("failed to save tmp sw", e);
        }

    }

    public void saveResult(ResultHitJsonSWM result, String index) {
        try {
            List<String> jsonStrs = new ArrayList();
            ObjectMapper mapper = new ObjectMapper();
            jsonStrs.add(mapper.writeValueAsString(result));
            ESSetter esSetter = new ESSetter(index);
            esSetter.putDocBulk(jsonStrs);
        } catch (Exception e) {
            logger.error("failed to same single result: ", e);
        }

    }

    public void saveResult(List<ResultHitJsonSWM> result, String index) {
        try {
            List<String> jsonStrs = new ArrayList();
            ObjectMapper mapper = new ObjectMapper();
            for (ResultHitJsonSWM r : result) {
                jsonStrs.add(mapper.writeValueAsString(r));
            }


            ESSetter esSetter = new ESSetter(index);
            esSetter.putDocBulk(jsonStrs);
        } catch (Exception e) {
            logger.error("failed to save bulk result: ", e);
        }

    }

}
