package com.Executables;

import com.esutil.PropertyReaderUtil;
import com.htmlNestedObjects.HtmlPreHandle;
import com.htmlThreeSentenses.HtmlThreeSentencePreHandle;

public final class HtmlToJson {

    public static void main(String[] args) {
        HtmlThreeSentencePreHandle htmlHandler = new HtmlThreeSentencePreHandle();
        htmlHandler.parceAllHtmlToJson(PropertyReaderUtil.INSTANCE.getProperty("path_to_html"), PropertyReaderUtil.INSTANCE.getProperty("path_to_json"));
    }
}