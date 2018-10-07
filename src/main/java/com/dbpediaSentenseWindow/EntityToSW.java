package com.dbpediaSentenseWindow;

import com.esutil.ESSetter;
import com.esutil.SentenseSpliter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityToSW {

    private Logger logger = LogManager.getLogger(EntityToSW.class);

    private WebdriverUtil webdriverUtil = new WebdriverUtil();

    private ESSetter esSetter = new ESSetter("dbpedia3s");

    String contextFileName = "existingEntities.txt";

    List<String> handledEntities = new ArrayList<>();

    public List<List<String>> getAllTriples() {
        List<List<String>> entityList = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource("dbpediaPoliticalTriplesSubset.txt");
            InputStream in = resource.getInputStream();

            if (in == null) {
                System.out.println("file not found.");
            }

            String content = IOUtils.toString(in, "UTF-8");

            List<String> rows = new ArrayList<String>(Arrays.asList(content.split("\\r?\\n")));

            for (String r : rows) {
                String[] triple = r.split("\\t");
                if (triple.length == 3) {
                    List<String> tri = Arrays.asList(triple);
                    entityList.add(tri);
                } else {
                    errors.add(r);
                }
            }
            if (errors.size() > 0) {
                logger.error("invalid triples: ");
                logger.error(StringUtils.collectionToDelimitedString(errors, "\\n"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return entityList;
    }

    public List<List<String>> getTriples(int start, int end) {
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

    private boolean saveSWsToES(List<DocJsonSWDBpedia> sws) {
        List<String> jsonStrs = new ArrayList();
        try {
            ObjectMapper mapper = new ObjectMapper();
            for (DocJsonSWDBpedia s : sws) {
                String json = mapper.writeValueAsString(s);
                jsonStrs.add(json);
            }
            return esSetter.putDocBulk(jsonStrs);
        } catch (Exception e) {
            logger.error(e);
            return false;
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
        List<DocJsonSWDBpedia> contents = new ArrayList<>();
        String derivedFrom = getDerivedFrom(entity);
        if (!StringUtils.hasText(derivedFrom)) {
            logger.error("failed to get derivedFrom for entity : " + entity);
            return contents;
        }

        String page = webdriverUtil.getPageContent(derivedFrom);
        if (!StringUtils.hasText(page)) {
            logger.error("failed to get page content for entity : " + entity);
            return contents;
        }

        List<String> sentences = SentenseSpliter.split(page);

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

    private List<String> getHandledEntitiesContext() {

        List<String> defalutNull = new ArrayList<>();
        try {
            String fileName = "existingEntities.txt";
            File file = new File(fileName);
            logger.info("File = " + file.getAbsolutePath());

            if (!file.exists()) {
                System.out.println("file not found. creating file");
                file.createNewFile();
                return new ArrayList<>();
            }

            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            return new ArrayList<String>(Arrays.asList(content.split("\\r?\\n")));
        } catch (Exception e) {
            logger.error(e);
            return defalutNull;
        }
    }

    private boolean isHandledEntity(String entity) {
        return handledEntities.contains(entity);
    }

    private void saveEntityToFile(String entity) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(contextFileName, true));
            writer.append(entity + "\n");
            writer.close();

        } catch (Exception e) {
            logger.error("file read or write error: " + entity, e);
        }
    }

    public void entitiesToESWorkflow(int startTripleIndex, int endTripleIndex) {
        List<List<String>> subset = getTriples(startTripleIndex, endTripleIndex);
        List<String> entities = getEntities(subset);

        //prepare context log file
        handledEntities = getHandledEntitiesContext();
        if (handledEntities == null || handledEntities.size() == 0) {
            logger.error("failed to prepare context log file, break");
            return;
        }

        for (String entity : entities) {
            if (!isHandledEntity(entity)) {
                List<DocJsonSWDBpedia> sws = generateSWForEntity(entity);
                logger.info("created sw for entity: " + entity);
                if (saveSWsToES(sws)) { //keep log for ES existing entities
                    saveEntityToFile(entity);
                }
            }
        }
    }

}
