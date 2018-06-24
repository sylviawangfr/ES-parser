package com.Executables;

import com.esutil.OWL2NT;
import com.htmlSentenceWindow.ESEngineSW;
import com.htmlSentenceWindow.ResultHitJsonSW;

import java.util.Arrays;
import java.util.List;

public final class SandBox {

    private SandBox() {
    }

    public static void main(String[] args) {

        OWL2NT converter = new OWL2NT();
        List<List<String>> tripples = converter.parseEntities();
        ESEngineSW esEngine = new ESEngineSW();
        for (List<String> tri : tripples) {
            List<ResultHitJsonSW> matchString = esEngine.searchEntitiesMatchString(tri);
            if (matchString.size() > 0) {
                esEngine.saveResult(matchString, "ibm3smatchstring");
            }
        }

    }


}