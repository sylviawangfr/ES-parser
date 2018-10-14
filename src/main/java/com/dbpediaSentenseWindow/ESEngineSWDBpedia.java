package com.dbpediaSentenseWindow;

import com.esutil.ESSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class ESEngineSWDBpedia {

    String originIndex = "dbpedia3s";
    String tmpTagIndex = "dbpedia3s-tmp-result";
    String resultIndex = "dbpedia3s-result";

    String defaultAnalyzer = "stop_analyzer";

    int size = 5;

    private Logger logger = LogManager.getLogger(ESSetter.class);

    private TagUtil tagUtil = new TagUtil();

    public ESEngineSWDBpedia withIndex(String newIndex) {
        this.originIndex = newIndex;
        return this;
    }

    public ESEngineSWDBpedia withResultIndex(String newIndex) {
        this.resultIndex = newIndex;
        return this;
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

    public List<ResultHitJsonSWDBpedia> getSWByEntitiesMixQuery(TransportClient client, List<String> entities) {
        try {
            List<ResultHitJsonSWDBpedia> results = new ArrayList<>();

            String default_field = "sentence";

            BoolQueryBuilder qb = QueryBuilders.boolQuery();
            if (StringUtils.hasText(entities.get(0))) {
                qb = qb.should(QueryBuilders.matchQuery(default_field, entities.get(0)).analyzer(defaultAnalyzer).boost(2f));
            }

//            if (StringUtils.hasText(entities.get(1))) {
//                qb = qb.should(QueryBuilders.moreLikeThisQuery(new String[]{default_field}, new String[]{entities.get(1)}, null).analyzer(entityAnalyzer).minTermFreq(1).maxQueryTerms(12));
//            }

            if (StringUtils.hasText(entities.get(2))) {
                qb = qb.should(QueryBuilders.moreLikeThisQuery(new String[]{default_field}, new String[]{entities.get(2)}, null).analyzer(defaultAnalyzer).minTermFreq(1).maxQueryTerms(12).boost(2f));
            }
            qb = qb.minimumShouldMatch("50%");

            SearchResponse response = client.prepareSearch(originIndex)
                    .setTypes("document")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setFrom(0)
                    .setSize(size)
                    .setQuery(qb)
                    .addSort("_score", SortOrder.DESC)
                    .execute().actionGet();

            for (SearchHit hit : response.getHits()) {
                Map map = hit.getSourceAsMap();
                ResultHitJsonSWDBpedia r = new ResultHitJsonSWDBpedia(map, entities, hit.getScore());
                results.add(r);
            }

            return results;

        } catch (Exception e) {
            logger.error("failed to put search. ", e);
            return null;
        }
    }

    public String tagHeadEntity(TransportClient client, ResultHitJsonSWDBpedia sw) {
        String taged = tagPhraseSearch(client, originIndex, sw, sw.head, "head");
        if (StringUtils.hasText(taged)) {
            return taged;
        } else {
            String reducedTail = reduceKeyword(sw.head);
            if (!reducedTail.equals(sw.head)) {
                return tagPhraseSearch(client, originIndex, sw, reducedTail, "head");
            }
        }
        return "";
    }

    public void sleepMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            System.out.println("interrupted");
        }
    }

    private String tagTailMatchSearch(TransportClient client, ResultHitJsonSWDBpedia sw) {
        try {
            BoolQueryBuilder qb = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchPhraseQuery("uri", sw.getUri()).slop(3).analyzer(defaultAnalyzer))
                    .must(QueryBuilders.termQuery("number", sw.number))
                    .must(QueryBuilders.matchQuery("sentence", sw.tail).analyzer(defaultAnalyzer));

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

    public String tagPhraseSearch(TransportClient client, String index, ResultHitJsonSWDBpedia sw, String keyword, String tag) {
        try {
            BoolQueryBuilder qb = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchPhraseQuery("uri", sw.getUri()).slop(3).analyzer(defaultAnalyzer))
                    .must(QueryBuilders.termQuery("number", sw.number))
                    .must(QueryBuilders.matchPhraseQuery("sentence", keyword).slop(10).analyzer(defaultAnalyzer));

            HighlightBuilder hb = new HighlightBuilder()
                    .preTags("<" + tag + ">")
                    .postTags("</" + tag + ">")
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

    private String reduceKeyword(String keyword) {
        if (keyword.contains("(")) {
            String trim = keyword.substring(keyword.indexOf("("), keyword.indexOf(")"));
            String reduceTail = keyword.replace(trim, "");
            return reduceTail;
        } else {
            return keyword;
        }
    }

    public String tagTailPhraseSearch(TransportClient client, ResultHitJsonSWDBpedia sw) {
        String taged = tagPhraseSearch(client, tmpTagIndex, sw, sw.tail, "tail");
        if (StringUtils.hasText(taged)) {
            return taged;
        } else {
            String reducedTail = reduceKeyword(sw.tail);
            if (!reducedTail.equals(sw.tail)) {
                return tagPhraseSearch(client, tmpTagIndex, sw, reducedTail, "tail");
            }
        }
        return "";
    }

    public List<ResultHitJsonSWDBpedia> getSWByEntitiesMatchQuery(TransportClient client, List<String> entities) {
        try {
            List<ResultHitJsonSWDBpedia> results = new ArrayList<>();

            //MultiMatchQueryBuilder qb = QueryBuilders.multiMatchQuery(sw.tail, "sentence").analyzer(entityAnalyzer);

            BoolQueryBuilder qb = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("head", entities.get(0)).analyzer(defaultAnalyzer))
                    //.must(QueryBuilders.matchQuery("label", entities.get(1)).analyzer(entityAnalyzer))
                    .must(QueryBuilders.matchQuery("tail", entities.get(2)).analyzer(defaultAnalyzer));


            SearchResponse response = client.prepareSearch(originIndex)
                    .setTypes("document")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setFrom(0)
                    .setSize(size)
                    .setQuery(qb)
                    .addSort("score", SortOrder.DESC)
                    .execute().actionGet();

            List<String> taggedSW = new ArrayList<>();
            for (SearchHit hit : response.getHits()) {
                Map map = hit.getSourceAsMap();
                ResultHitJsonSWDBpedia r = new ResultHitJsonSWDBpedia(map, entities, hit.getScore());
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
            List<ResultHitJsonSWDBpedia> sws = getSWByEntitiesMixQuery(client, entities);

            if (sws == null) {
                sws = getSWByEntitiesMatchQuery(client, entities);
            }

            for (ResultHitJsonSWDBpedia sw : sws) {
                ResultHitJsonSWDBpedia swTag = new ResultHitJsonSWDBpedia(sw);
                String taggedHeadSW = tagHeadEntity(client, sw);
                if (StringUtils.hasText(taggedHeadSW)) {
                    taggedHeadSW = tagUtil.mergeTags(taggedHeadSW);
                    swTag.setSentence(taggedHeadSW);
                    sleepMillis(500);
                    saveTmpTag(swTag);
                    sleepMillis(1000);


                    String taggedTailSW = tagTailPhraseSearch(client, swTag);
                    if (!StringUtils.hasText(taggedTailSW)) {
                        taggedTailSW = tagTailMatchSearch(client, swTag);
                    }

                    if (StringUtils.hasText(taggedTailSW)) {
                        taggedTailSW = tagUtil.mergeTags(taggedTailSW);
                        taggedTailSW = tagUtil.tagNearest(taggedTailSW);
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


    public void saveTmpTag(ResultHitJsonSWDBpedia sw) {
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

    public void saveResult(ResultHitJsonSWDBpedia result, String index) {
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
}
