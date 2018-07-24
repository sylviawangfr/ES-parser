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
        esEngine = esEngine.withResultIndex("3smatchobjectonly");
        int i = 1;
        for (List<String> tri : tripples) {
            List<ResultHitJsonSWM> matchString = esEngine.searchEntitiesMixQuery(tri);
            if (matchString.size() > 0) {
                esEngine.saveResult(matchString);
                i++;
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("interrupted");
                }
            }

            if (i > 500) {
                break;
            }
        }
    }
}