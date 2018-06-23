package com.htmlNestedObjects;

import com.ValueTypes.HtmlConvertor;
import com.esutil.ESSetter;
import com.esutil.PropertyReaderUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.htmlparser.HtmlParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HtmlPreHandle implements HtmlConvertor {

    private Logger logger = LogManager.getLogger(HtmlPreHandle.class);

    String index = "ibmnested2";

    public HtmlPreHandle withIndex(String newIndex) {
        this.index = newIndex;
        return this;
    }

    public String parseHtml(File file) {
        try {
            HtmlParser htmlParser = new HtmlParser();
            Document doc = Jsoup.parse(file, "UTF-8");
            DocJsonNestedObject docJson = new DocJsonNestedObject();
            docJson.setBuiltinMeta(htmlParser.parseMeta(doc));
            docJson.setRelated_links(htmlParser.parseRelatedLinks(doc));
            docJson.setContent(htmlParser.parseBody(doc));
            docJson.setHead(htmlParser.parseHead(doc));

            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(docJson);
            return jsonInString;

        } catch (IOException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public void parseAllJsonToES(String pathToJson) {
        ESSetter esSetter = new ESSetter(index);
        esSetter.putDocBulk(PropertyReaderUtil.INSTANCE.getProperty("path_to_json"));
    }


    public void parseAllHtmlToJson(String pathToHtml, String pathToJson) {
        try {

            File directory = new File(pathToJson);
            if (!directory.exists()) {
                directory.mkdir();
            }

            File dir = new File(pathToHtml);

            if (dir.exists()) {
                for (File file : dir.listFiles()) {
                    if (file.isFile()) {
                        String json = parseHtml(file);
                        String name = pathToJson + file.getName().replace("html", "json");
                        saveToJsonFile(json, name);
                    }
                }
            } else {
                logger.error("failed to load origin html file, please check path");
            }
        } catch (Exception e) {
            logger.error("failed to load sample data");
        }
    }

    public void parseAllHtmlToES(String pathToHtml) {
        try {
            File dir = new File(pathToHtml);
            List<String> jsonStrs = new ArrayList();
            if (dir.exists()) {
                for (File file : dir.listFiles()) {
                    if (file.isFile()) {
                        String json = parseHtml(file);
                        jsonStrs.add(json);
                    }
                }
                ESSetter esSetter = new ESSetter(index);
                esSetter.putDocBulk(jsonStrs);
            } else {
                logger.error("failed to load origin html file, please check path");
            }
        } catch (Exception e) {
            logger.error("failed to load sample data: ", e);
        }
    }


    private void saveToJsonFile(String json, String fileName) {
        Path path = Paths.get(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(json);
        } catch (Exception e) {
            logger.error("failed to write json to file");
        }
    }

}
