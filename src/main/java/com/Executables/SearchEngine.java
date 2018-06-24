package com.Executables;

import com.htmlSentenceWindow.ESEngineSW;
import com.htmlSentenceWindow.ResultHitJsonSW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SearchEngine {


    public static void main(String[] args) {
        ESEngineSW esEngine = new ESEngineSW();
        //<http://www.semanticweb.org/watson/ontologies/2017/1/it_kg_version1#da_component_php>
        //<http://www.semanticweb.org/watson/ontologies/2017/1/it_kg_version1#has_task>
        //<http://www.semanticweb.org/watson/ontologies/2017/1/it_kg_version1#da_task_ibm_digital_analytics_server_side_plug_in_tag_api_for_php> .
        List<String> entities = Arrays.asList("component_php", "task_ibm_digital_analytics_server_side_plug_in_tag_api_for_php");
       // List<ResultHitJsonSW> matchPhrase = esEngine.searchEntitiesMatchPhrase(entities);
        List<ResultHitJsonSW> matchString = esEngine.searchEntitiesMatchString(entities);
    }
}