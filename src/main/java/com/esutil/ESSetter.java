package com.esutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

//todo: set mapper, filter, index docs etc.
public class ESSetter {

    String index;

    private Logger logger = LogManager.getLogger(ESSetter.class);

    public ESSetter(String indexName) {
        index = indexName;
    }

    private String callRest(String requestUrl, String requestMethod) {
        String result = "";
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            while ((output = br.readLine()) != null) {
                result += output;
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            logger.debug("!!!MalformedURLException!!!");
            e.printStackTrace();
        } catch (IOException e) {
            logger.debug("!!!IOException!!!>");
            e.printStackTrace();
        }
        return result;
    }

    private String readFileToStr(File file) {

        StringBuilder result = new StringBuilder("");

        try (InputStream inputStream = new FileInputStream(file)) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String postRequest(String urlStr, String jsonStr) {
        String result = "";
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(jsonStr.getBytes());
            os.flush();

            for (int c = 0; c < 10; c++) {
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) break;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                result += output;
            }
            os.close();
            br.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    public void putDoc() {
        try {
            //File input = new File("./src/main/resources/Error event IDs and error codes.html");
            File dir = new File(PropertyReaderUtil.INSTANCE.getProperty("path_to_json"));
            String url = "http://localhost:9200/" + index + "/document/";
            int docNumber = 1;
            if (dir.exists()) {
                for (File file : dir.listFiles()) {
                    if (file.isFile()) {

                        String json = readFileToStr(file);
                        postRequest(url + String.valueOf(docNumber), json);
                        docNumber++;
                    }
                }
            } else {
                logger.error("failed to load origin html file, please check path");
            }
        } catch (Exception e) {
            logger.error("failed to load sample data");
        }

    }

    public void putDocBulk(String pathToJson) {
        try {
            //File input = new File("./src/main/resources/Error event IDs and error codes.html");
            File dir = new File(pathToJson);
            String meta = "{ \"index\": {}}" + "\n";
            String url = "http://localhost:9200/" + index + "/document/_bulk/";
            if (dir.exists()) {
                File[] listOfFiles = dir.listFiles();
                int j = 0;
                while (j < listOfFiles.length) {
                    int i = 0;
                    String bulkRequestJson = "";
                    while (i < 10 && j < listOfFiles.length) {
                        File file = listOfFiles[j];
                        if (file.isFile() && file.getName().contains(".json")) {
                            String json = readFileToStr(file);
                            bulkRequestJson = bulkRequestJson + meta + json + "\n";
                            i++;
                        }
                        j++;
                    }
                    postRequest(url, bulkRequestJson);
                }
            } else {
                logger.error("failed to load origin html file, please check path");
            }
        } catch (Exception e) {
            logger.error("failed to load sample data: ", e);
        }
    }

    public boolean putDocBulk(List<String> jsonStrs) {
        try {
            String meta = "{ \"index\": {}}" + "\n";
            String url = "http://localhost:9200/" + index + "/document/_bulk/";

            int j = 0;
            while (j < jsonStrs.size()) {
                int i = 0;
                String bulkRequestJson = "";
                while (i < 10 && j < jsonStrs.size()) {
                    String json = jsonStrs.get(j);
                    bulkRequestJson = bulkRequestJson + meta + json + "\n";
                    i++;
                    j++;
                }
                postRequest(url, bulkRequestJson);
            }
            return true;

        } catch (Exception e) {
            logger.error("failed to load sample data: ", e);
            return false;
        }
    }

}
