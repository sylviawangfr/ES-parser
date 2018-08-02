package com.Executables;

import com.esutil.OWL2NT;
import com.htmlSentenceWindow.ESEngineSW;
import com.htmlSentenceWindow.ResultHitJsonSW;
import com.htmlSentenceWindowWithMeta.ESEngineSWM;
import com.htmlSentenceWindowWithMeta.ResultHitJsonSWM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SearchEngine {


    public static void main(String[] args) {
        OWL2NT converter = new OWL2NT();
        List<List<String>> tripples = converter.parseEntitiesObjectOnly();

        ESEngineSWM esEngine = new ESEngineSWM();

        for (List<String> tri : tripples) {
            List<ResultHitJsonSWM> matchString = esEngine.searchEntitiesMixQuery(tri);
            if (matchString.size() > 0) {

//            esEngine.saveResult(matchString);
//            List<String> tri = new ArrayList<>();
//            tri.add("da_component_websphere_portal_integration");
//            tri.add("has_task");
//            tri.add("da_task_websphere_portal_integration");

                esEngine.searchAndTagAndSave(tri);

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    System.out.println("interrupted");
                }
            }
        }
    }
}