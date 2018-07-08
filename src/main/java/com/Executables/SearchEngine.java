package com.Executables;

import com.htmlSentenceWindow.ESEngineSW;
import com.htmlSentenceWindow.ResultHitJsonSW;
import com.htmlSentenceWindowWithMeta.ESEngineSWM;
import com.htmlSentenceWindowWithMeta.ResultHitJsonSWM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SearchEngine {


    public static void main(String[] args) {
        ESEngineSWM esEngine = new ESEngineSWM();
        //<http://www.semanticweb.org/watson/ontologies/2017/1/it_kg_version1#da_component_php>
        //<http://www.semanticweb.org/watson/ontologies/2017/1/it_kg_version1#has_task>
        //<http://www.semanticweb.org/watson/ontologies/2017/1/it_kg_version1#da_task_ibm_digital_analytics_server_side_plug_in_tag_api_for_php> .
        //List<String> entities = Arrays.asList("component_php", "", "task_ibm_digital_analytics_server_side_plug_in_tag_api_for_php");
        //List<ResultHitJsonSWM> matchPhrase = esEngine.searchEntitiesMatchPhrase(entities);
        //List<ResultHitJsonSWM> matchString = esEngine.searchEntitiesMixQuery(entities);
    }
}