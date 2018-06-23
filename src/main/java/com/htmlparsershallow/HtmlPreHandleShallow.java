package com.htmlparsershallow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class HtmlPreHandleShallow {

    private Logger logger = LogManager.getLogger(HtmlPreHandleShallow.class);

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
