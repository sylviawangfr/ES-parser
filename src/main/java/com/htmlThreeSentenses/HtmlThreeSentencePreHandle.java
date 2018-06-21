package com.htmlThreeSentenses;

import com.ValueTypes.HtmlConvertor;
import com.ValueTypes.Sentence;
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
import java.util.Arrays;
import java.util.List;

public class HtmlThreeSentencePreHandle implements HtmlConvertor {

    private Logger logger = LogManager.getLogger(HtmlThreeSentencePreHandle.class);

    String index = "ibm3s";

    public HtmlThreeSentencePreHandle withIndex(String newIndex) {
        this.index = newIndex;
        return this;
    }

    private List<DocJsonThreeSentences> parseHtmlToSentenceWindows(File file) {
        List<DocJsonThreeSentences> contents = new ArrayList<>();
        try {
            String name = file.getName();
            HtmlParser htmlParser = new HtmlParser();
            Document doc = Jsoup.parse(file, "UTF-8");
            List<Sentence> sentences = Arrays.asList(htmlParser.parseBody(doc));
            int i = 0;
            int j = 0;
            int length = sentences.size();

            while(i < length) {
                String window = "";
                while(j < length && j < i + 3) {
                    window = window + sentences.get(j).getSentence();
                    j++;
                }
                DocJsonThreeSentences json = new DocJsonThreeSentences();
                json.setFileName(name);
                json.setSentence(window);
                json.setNumber(i);
                contents.add(json);

                if (j == length) {
                    break;
                } else {
                    i = i + 2;
                    j = i;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return contents;
        }
    }

    public String parseHtml(File file) {
        return "";
    }

    public void parceAllJsonToES(String pathToJson) {
        ESSetter esSetter = new ESSetter(index);
        esSetter.putDocBulk(PropertyReaderUtil.INSTANCE.getProperty("path_to_json"));

    }


    public void parceAllHtmlToJson(String pathToHtml, String pathToJson) {
        try {

            File directory = new File(pathToJson);
            if (!directory.exists()) {
                directory.mkdir();
            }

            File dir = new File(pathToHtml);

            if (dir.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                for (File file : dir.listFiles()) {
                    if (file.isFile()) {
                        List<DocJsonThreeSentences> jsonObjs = parseHtmlToSentenceWindows(file);
                        for(DocJsonThreeSentences s : jsonObjs) {
                            String json = mapper.writeValueAsString(s);
                            String name = pathToJson + file.getName().replace(".html", "_" + String.valueOf(s.getNumber()) + ".json");
                            saveToJsonFile(json, name);
                        }

                    }
                }
            } else {
                logger.error("failed to load origin html file, please check path");
            }
        } catch (Exception e) {
            logger.error("failed to load sample data");
        }
    }

    public void parceAllHtmlToES(String pathToHtml) {
        try {
            File dir = new File(pathToHtml);
            List<String> jsonStrs = new ArrayList();
            if (dir.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                for (File file : dir.listFiles()) {
                    if (file.isFile()) {
                        List<DocJsonThreeSentences> jsonObjs = parseHtmlToSentenceWindows(file);
                        for(DocJsonThreeSentences s : jsonObjs) {
                            String json = mapper.writeValueAsString(s);
                            jsonStrs.add(json);
                        }
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
