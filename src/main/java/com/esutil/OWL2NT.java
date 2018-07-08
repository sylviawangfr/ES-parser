package com.esutil;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OWL2NT {

    public void convert() {

        try {
            ClassPathResource resource = new ClassPathResource("IBMOntMay.owl");
            InputStream in = resource.getInputStream();

            if (in == null) {
                System.out.println("file not found.");
            }

            Model model = ModelFactory.createDefaultModel();
            model.read(in, null);

            String fileName = "ibm_kg.nt";
            FileWriter out = new FileWriter(fileName);
            model.write( out, "N-TRIPLES");
            out.close();
            model.write(System.out, "N-TRIPLES");
        } catch (Exception e) {
            System.out.println("failed to read file to model.");
        }
    }
    private String trimEntity(String entityOrigin) {
        String regex = "(?<=#)(.*)(?=>)";

        Pattern p = Pattern.compile(regex);   // the pattern to search for
        Matcher m = p.matcher(entityOrigin);

        // if we find a match, get the group
        if (m.find())
        {
            // we're only looking for one group, so get it
            String theGroup = m.group();
            return theGroup;
        } else {
            return entityOrigin.replace("\"", "");
        }

    }

//    public List<List<String>> parseEntities() {
//
//        List<List<String>> entityList = new ArrayList<>();
//        try {
//            ClassPathResource resource = new ClassPathResource("ibm_kg.nt");
//            InputStream in = resource.getInputStream();
//
//            if (in == null) {
//                System.out.println("file not found.");
//            }
//
//            String content = IOUtils.toString(in, "UTF-8");
//            String [] tripples = content.split(" .\n");
//
//            for (int i = 0; i < tripples.length; i++) {
//                String tri = tripples[i].trim();
//                String [] entities = tri.split(" ");
//                List<String> oneTri = new ArrayList<>();
//                for (int j = 0; j < entities.length; j++) {
//                    String entity = trimEntity(entities[j]);
//                    oneTri.add(entity);
//                }
//                entityList.add(oneTri);
//            }
//
//        } catch (Exception e) {
//            System.out.println("failed to read file to model.");
//
//        }
//        return entityList;
//    }

    public List<List<String>> parseEntities() {
        List<List<String>> entityList = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource("ibm_kg.nt");
            InputStream in = resource.getInputStream();

            if (in == null) {
                System.out.println("file not found.");
            }

            String content = IOUtils.toString(in, "UTF-8");

            List<String> rows = new ArrayList<String>(Arrays.asList(content.split(" .\n")));

            for (int i = 0; i < rows.size(); i++) {
                String tmptriple = rows.get(i).replace("> \"", ">LETSPLIT\"");
                tmptriple = tmptriple.replace("> <", ">LETSPLIT<");
                tmptriple = tmptriple.replace("\" <", "\"LETSPLIT<");
                tmptriple = tmptriple.replace("\" \"", "\"LETSPLIT\"");
                tmptriple = tmptriple.replace(" <http", "LETSPLIT<http");
                tmptriple = tmptriple.replace("> _:", ">LETSPLIT<");
                String[] triple = tmptriple.split("LETSPLIT");
                if (triple.length == 3) {
                    List<String> tri = new ArrayList<>();
                    for (int j = 0; j < 3; j++) {
                        tri.add(trimEntity(triple[j]));
                    }
                    entityList.add(tri);
                } else {
                    errors.add(String.valueOf(i) + " " + rows.get(i));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return entityList;
    }

}
