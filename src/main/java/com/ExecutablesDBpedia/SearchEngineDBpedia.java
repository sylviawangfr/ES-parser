package com.ExecutablesDBpedia;

import com.dbpediaSentenseWindow.ESEngineSWDBpedia;
import com.dbpediaSentenseWindow.EntityToSW;
import com.dbpediaSentenseWindow.TagUtil;
import com.esutil.OWL2NT;
import com.htmlSentenceWindowWithMeta.ESEngineSWM;

import java.util.ArrayList;
import java.util.List;

public final class SearchEngineDBpedia {


    public static void main(String[] args) {
        //EntityToSW entityToSW = new EntityToSW();


        //List<List<String>> tris = entityToSW.getTriples(0, 50);
        //ESEngineSWDBpedia esEngine = new ESEngineSWDBpedia();

        String s = "<head> test1 </head> test2 <tail> test3 </tail> <head> test4 </head> ddd5 <tail> test6 </tail> tt <head> test7 </head>";
        TagUtil tagUtil = new TagUtil();
        tagUtil.mergeTagsNearest(s);

//        int i = 0;
//        try {
//            for (int j = 0; j < tris.size(); j++) {
//                List<String> tri = tris.get(j);
//                List<String> trimedTri = trimEntity(tri);
//                esEngine.searchAndTagAndSave(trimedTri);
//
//                try {
//                    Thread.sleep(100);
//                } catch (Exception e) {
//                    System.out.println("interrupted");
//                }
//
//                i = j;
//                System.out.println("last triple index: " + String.valueOf(i));
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//            throw e;
//        } finally {
//            System.out.println("last triple index: " + String.valueOf(i));
//        }
    }

    private static List<String> trimEntity(List<String> uris) {
        List<String> trimedTri = new ArrayList<>();
        for (String s : uris) {
            String[] pieces = s.split("/");
            trimedTri.add(pieces[pieces.length - 1]);
        }
        return trimedTri;
    }
}