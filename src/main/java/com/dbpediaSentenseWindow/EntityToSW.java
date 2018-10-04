package com.dbpediaSentenseWindow;

import com.esutil.ESSetter;
import com.esutil.SentenseSpliter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityToSW {

    private Logger logger = LogManager.getLogger(EntityToSW.class);

    private WebdriverUtil webdriverUtil = new WebdriverUtil();

    private ESSetter esSetter = new ESSetter("dbpedia3s");

    public List<List<String>> getAllTriples() {
        List<List<String>> entityList = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource(".txt");
            InputStream in = resource.getInputStream();

            if (in == null) {
                System.out.println("file not found.");
            }

            String content = IOUtils.toString(in, "UTF-8");

            List<String> rows = new ArrayList<String>(Arrays.asList(content.split(" \n")));

            for (String r : rows) {
                String[] triple = r.split(" ");
                if (triple.length == 3) {
                    List<String> tri = Arrays.asList(triple);
                    entityList.add(tri);
                } else {
                    errors.add(r);
                }
            }
            if (errors.size() > 0) {
                logger.error("invalid triples: ");
                logger.error(StringUtils.join(errors, "\n"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return entityList;
    }

    private List<List<String>> getTriples(int start, int end) {
        List<List<String>> all = getAllTriples();
        return all.subList(start, end);
    }

    private List<String> getEntities(List<List<String>> triples) {
        List<String> entities = new ArrayList<>();
        for (List<String> l : triples) {
            if (!entities.contains(l.get(0))) {
                entities.add(l.get(0));
            }

            if (!entities.contains(l.get(2))) {
                entities.add(l.get(2));
            }
        }
        return entities;
    }

    private void saveSWsToES(List<DocJsonSWDBpedia> sws) {
        List<String> jsonStrs = new ArrayList();
        try {
            ObjectMapper mapper = new ObjectMapper();
            for (DocJsonSWDBpedia s : sws) {
                String json = mapper.writeValueAsString(s);
                jsonStrs.add(json);
            }
            esSetter.putDocBulk(jsonStrs);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private String getDerivedFrom(String entity) {
        String uri = entity;
        String prefix = "http://dbpedia.org/resource/";
        if (entity.contains(prefix)) {
            uri = entity.replace(prefix, "http://dbpedia.org/page/");
        }
        return webdriverUtil.getDerivedFrom(uri);
    }

    private List<DocJsonSWDBpedia> generateSWForEntity(String entity) {
        String derivedFrom = getDerivedFrom(entity);
        String page = webdriverUtil.getPageContent(derivedFrom);
        List<String> sentences = SentenseSpliter.split(page);

        List<DocJsonSWDBpedia> contents = new ArrayList<>();
        int i = 0;
        int j = 0;
        int length = sentences.size();
        while (i < length) {
            String window = "";
            while (j < length && j < i + 3) {
                window = window + sentences.get(j);
                j++;
            }
            DocJsonSWDBpedia json = new DocJsonSWDBpedia();
            json.setUri(entity);
            json.setSentence(window);
            json.setNumber(i);
            json.setDerivedFrom(derivedFrom);
            json.setEntityName(entity);
            contents.add(json);

            if (j == length) {
                break;
            } else {
                i = i + 1;
                j = i;
            }
        }
        return contents;
    }

    public void entitiesToESWorkflow(int startTripleIndex, int endTripleIndex) {
        List<List<String>> subset = getTriples(startTripleIndex, endTripleIndex);
        List<String> entities = getEntities(subset);
        for (String entity : entities) {
            List<DocJsonSWDBpedia> sws = generateSWForEntity(entity);
            saveSWsToES(sws);
        }
    }

}
