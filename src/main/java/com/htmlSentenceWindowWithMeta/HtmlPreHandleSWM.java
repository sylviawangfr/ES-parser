package com.htmlSentenceWindowWithMeta;

import com.ValueTypes.HtmlConvertor;
import com.ValueTypes.MetaData;
import com.ValueTypes.Sentence;
import com.esutil.ESSetter;
import com.esutil.HtmlParser;
import com.esutil.PropertyReaderUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

public class HtmlPreHandleSWM implements HtmlConvertor {

    private Logger logger = LogManager.getLogger(HtmlPreHandleSWM.class);

    String index = "ibm3s-with-meta";

    public HtmlPreHandleSWM withIndex(String newIndex) {
        this.index = newIndex;
        return this;
    }


    private List<DocJsonSWM> parseHtmlToSentenceWindows(File file) {
        List<DocJsonSWM> contents = new ArrayList<>();
        try {
            String name = file.getName();
            HtmlParser htmlParser = new HtmlParser();
            Document doc = htmlParser.parseFile(file);
            MetaData metaData = htmlParser.parseMeta(doc);
            List<Sentence> sentences = Arrays.asList(htmlParser.parseBody(doc));
            int i = 0;
            int j = 0;
            int length = sentences.size();

            while (i < length) {
                String window = "";
                while (j < length && j < i + 3) {
                    window = window + sentences.get(j).getSentence();
                    j++;
                }
                DocJsonSWM json = new DocJsonSWM();
                json.setFileName(name);
                json.setSentence(window);
                json.setNumber(i);
                json.setDescription(metaData.getDescription());
                json.setTitle(metaData.getTitle());
                contents.add(json);

                if (j == length) {
                    break;
                } else {
                    i = i + 1;
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

    public void parseAllJsonToES(String pathToJson) {
        ESSetter esSetter = new ESSetter(index);
        esSetter.putDocBulk(PropertyReaderUtil.INSTANCE.getProperty("path_to_json_swm"));

    }


    public void parseAllHtmlToJson(String pathToHtml, String pathToJson) {
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
                        List<DocJsonSWM> jsonObjs = parseHtmlToSentenceWindows(file);
                        for (DocJsonSWM s : jsonObjs) {
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

    private List<File> getAllFilesFromPath(String directoryName) {
        // get all the files from a directory
        File directory = new File(directoryName);
        List<File> resultList = new ArrayList<File>();
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isDirectory()) {
                resultList.addAll(getAllFilesFromPath(file.getAbsolutePath()));
            } else {
                if (file.isFile() && file.getName().contains("html")) {
                    resultList.add(file);
                }
            }
        }
        return resultList;
    }

    public void parseAllHtmlToES(String pathToHtml) {
        try {
            List<File> listOfFile = getAllFilesFromPath(pathToHtml);
            List<String> jsonStrs = new ArrayList();

            ObjectMapper mapper = new ObjectMapper();
            for (File file : listOfFile) {
                if (file.isFile()) {
                    List<DocJsonSWM> jsonObjs = parseHtmlToSentenceWindows(file);
                    for (DocJsonSWM s : jsonObjs) {
                        String json = mapper.writeValueAsString(s);
                        jsonStrs.add(json);
                    }

                }
            }
            ESSetter esSetter = new ESSetter(index);
            esSetter.putDocBulk(jsonStrs);

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
