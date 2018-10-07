package com.ExecutablesIBM;

import com.esutil.OWL2NT;
import com.htmlSentenceWindowWithMeta.ESEngineSWM;

import java.util.ArrayList;
import java.util.List;

public final class SearchEngine {


    public static void main(String[] args) {
        OWL2NT converter = new OWL2NT();
        List<List<String>> tripples = converter.parseEntities();

        ESEngineSWM esEngine = new ESEngineSWM();
        int i = 0;
        try {

            List<List<String>> tris = getLabelTriples(tripples, "error_description");
            for (int j = 0; j < tris.size(); j++) {
                List<String> tri = tris.get(j);

                esEngine.searchAndTagAndSave(tri);

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    System.out.println("interrupted");
                }

                i = j;
                System.out.println("last triple index: " + String.valueOf(i));
            }
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        } finally {
            System.out.println("last triple index: " + String.valueOf(i));
        }
    }

    static private List<List<String>> getLabelTriples(List<List<String>> triples, String label) {

        List<List<String>> treats = new ArrayList<>();
        for (List<String> tri : triples) {
            if (tri.get(1).equals(label)) {
                treats.add(tri);
            }
        }
        return treats;

    }
}