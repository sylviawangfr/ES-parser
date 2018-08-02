package com.esutil;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ToOpenKE {

    public void convert(List<List<String>> tris) {

        List<String> entities = new ArrayList<String>();
        List<String> relations = new ArrayList<String>();
        List<String> triples = new ArrayList<String>();
        List<String> rows = new ArrayList<String>();

        for (int i = 0; i < tris.size(); i++) {
            List<String> tri = tris.get(i);
            if (!entities.contains(tri.get(0))) {
                entities.add(tri.get(0));
            }

            if (!relations.contains(tri.get(1))) {
                relations.add(tri.get(1));
            }

            if (!entities.contains(tri.get(2))) {
                entities.add(tri.get(2));
            }

            String convertedTriple = String.format("%s %s %s", entities.indexOf(tri.get(0)), entities.indexOf(tri.get(2)), relations.indexOf(tri.get(1)));
            triples.add(convertedTriple);

        }

        List<String> entitiesNumbered = new ArrayList<String>();
        List<String> relationsNumbered = new ArrayList<String>();
        for (int i = 0; i < entities.size(); i++) {
            entitiesNumbered.add(entities.get(i) + "    " + String.valueOf(i));
        }

        for (int i = 0; i < relations.size(); i++) {
            relationsNumbered.add(relations.get(i) + "    " + String.valueOf(i));
        }

        entitiesNumbered.add(0, String.valueOf(entities.size()));
        relationsNumbered.add(0, String.valueOf(relations.size()));

        writeListToFile(entitiesNumbered, "entity2id.txt");
        writeListToFile(relationsNumbered, "relation2id.txt");

        int tripleSize = triples.size();
        triples.add(0, String.valueOf(tripleSize));
        writeListToFile(triples, "train2id.txt");
    }

    public void writeListToFile(List<String> rows, String fileName) {
        try {
            FileUtils.writeLines(new File(fileName), "UTF-8", rows);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
