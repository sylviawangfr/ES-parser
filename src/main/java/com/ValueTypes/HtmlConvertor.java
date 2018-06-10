package com.ValueTypes;

import java.io.File;

public interface HtmlConvertor {
    String parseHtml(File file);
    void parceAllHtmlToJson(String pathToOrigin, String pathToDest);
    void parceAllHtmlToES(String pathToFolder);
}
