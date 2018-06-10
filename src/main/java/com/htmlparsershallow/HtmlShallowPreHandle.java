package com.htmlparsershallow;

import com.ValueTypes.Link;
import com.ValueTypes.MetaData;
import com.ValueTypes.RelatedLinks;
import com.esutil.PropertyReaderUtil;
import com.esutil.SentenseSpliter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class HtmlShallowPreHandle {

    private Logger logger = LogManager.getLogger(HtmlShallowPreHandle.class);

//    public void parceAllHtml() {
//        try {
//            //File input = new File("./src/main/resources/Error event IDs and error codes.html");
//            File dir = new File(PropertyReaderUtil.INSTANCE.getProperty("path_to_html"));
//            String jsonPath = PropertyReaderUtil.INSTANCE.getProperty("path_to_json");
//
//            if (dir.exists()) {
//                for (File file : dir.listFiles()) {
//                    if (file.isFile()) {
//                        String json = parseHtml(file);
//                        String name = jsonPath + file.getName().replace("html", "json");
//                        saveToJsonFile(json, name);
//                    }
//                }
//            } else {
//                logger.error("failed to load origin html file, please check path");
//            }
//        } catch (Exception e) {
//            logger.error("failed to load sample data");
//        }
//    }

    private void saveToJsonFile(String json, String fileName) {
        Path path = Paths.get(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(path))
        {
            writer.write(json);
        } catch (Exception e) {
            logger.error("failed to write json to file");
        }
    }

//    public String parseHtml(File file) {
//        try {
//            Document doc = Jsoup.parse(file, "UTF-8");
//            DocJsonShallow docJson = new DocJsonShallow();
//            docJson.setBuiltinMeta(parseMeta(doc));
//            docJson.setRelated_links(parseRelatedLinks(doc));
//            docJson.setSentence(parseSentences(doc));
//            docJson.setHead(parseHead(doc));
//
//            ObjectMapper mapper = new ObjectMapper();
//            String jsonInString = mapper.writeValueAsString(docJson);
//            return jsonInString;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "[]";
//        }
//    }

}
