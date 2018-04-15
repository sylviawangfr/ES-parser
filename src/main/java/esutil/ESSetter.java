package esutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//todo: set mapper, filter, index docs etc.
public class ESSetter {

    private Logger logger = LogManager.getLogger(ESSetter.class);

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

}
