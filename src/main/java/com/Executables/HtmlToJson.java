package com.Executables;

import com.esutil.PropertyReaderUtil;
import com.htmlNestedObjects.HtmlPreHandle;

public final class HtmlToJson {

    public static void main(String[] args) {
        HtmlPreHandle htmlHandler = new HtmlPreHandle();
        htmlHandler.parceAllHtmlToJson(PropertyReaderUtil.INSTANCE.getProperty("path_to_html"), PropertyReaderUtil.INSTANCE.getProperty("path_to_json"));
    }
}