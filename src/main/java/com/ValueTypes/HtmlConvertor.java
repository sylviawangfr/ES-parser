package com.ValueTypes;

import java.io.File;

public interface HtmlConvertor {
    String parseHtml(File file);
    void parseAllHtmlToJson(String pathToOrigin, String pathToDest);
    void parseAllHtmlToES(String pathToFolder);
    void parseAllJsonToES(String pathToJson);
}
