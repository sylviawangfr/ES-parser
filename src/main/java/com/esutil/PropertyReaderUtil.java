package com.esutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public enum PropertyReaderUtil {

    INSTANCE;

    private Properties properties;

    private String file = "src/main/resources/config.properties";
    private Logger logger = LogManager.getLogger(PropertyReaderUtil.class);

    PropertyReaderUtil() {
        loadPropertyFile();
    }

    private void loadPropertyFile() {
        InputStream inputStream = null;
        try {
            properties = new Properties();
                inputStream = new FileInputStream(file);
                properties.load(inputStream);

        } catch (Exception e) {
            logger.error(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public String getProperty(String key) {
        if (properties == null) {
            loadPropertyFile();
        }

        return null == System.getProperty(key) ? properties.getProperty(key) : System.getProperty(key);
    }

}
