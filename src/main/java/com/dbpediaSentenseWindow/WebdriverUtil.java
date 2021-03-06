package com.dbpediaSentenseWindow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;

public class WebdriverUtil {

    ChromeDriver driver;
    private Logger logger = LogManager.getLogger(WebdriverUtil.class);

    By paragraph = By.tagName("p");
    By derivedFrom = By.cssSelector("a[rel='prov:wasDerivedFrom']");

    private ChromeDriver getDriver() {
        ChromeOptions options = getChromeOptions();
        return new  ChromeDriver(options);

    }

    private ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-confirmation");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--use-fake-ui-for-media-stream");
        options.addArguments("--use-fake-device-for-media-stream");
        options.addArguments("--disable-logging");
        options.addArguments("--log-level=3");
        options.addArguments("--no-sandbox");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--disable-infobars");
        options.addArguments("--allow-cross-origin-auth-prompt");
        //options.addArguments("--headless");
        return options;
    }

    public String getPageContent(String url) {
        if (!openPage(url)) {
            return "";
        }
        waitForElementPresent(5, By.id("bodyContent"));
        List<WebElement> eles =  driver.findElements(paragraph);
        String content = "";
        for (WebElement e : eles) {
            String para = e.getText();
            content = content + para;
        }
        return content;
    }

    public String getDerivedFrom(String url) {
        if (!openPage(url)) {
            return "";
        }

        waitForElementPresent(5, derivedFrom);
        List<WebElement> eles =  driver.findElements(derivedFrom);
        if (eles != null && eles.size() > 0) {
            return eles.get(0).getAttribute("href");
        } else {
            return "";
        }
    }

    private void waitForElementPresent(long timeout, By by) {
        try {
            new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            logger.error("element waiting timeout: " + by.toString());
        }
    }

    private boolean openPage(String url) {
        if (driver == null) {
            driver = getDriver();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    if (driver != null) {
                        driver.close();
                    }
                }
            });
        }
        try {
            driver.get(url);
            return true;
        } catch (Exception e) {
            driver.close();
            driver = null;
            logger.error("failed to open url : " + url);
            return false;
        }
    }
}
