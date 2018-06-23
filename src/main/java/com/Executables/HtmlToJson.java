package com.Executables;

import com.ValueTypes.HtmlConvertor;
import com.esutil.PropertyReaderUtil;
import com.htmlThreeSentenses.HtmlThreeSentencePreHandle;

public final class HtmlToJson {

    public static void main(String[] args) {
        HtmlConvertor htmlHandler = new HtmlThreeSentencePreHandle();
        htmlHandler.parseAllHtmlToJson(PropertyReaderUtil.INSTANCE.getProperty("path_to_html"), PropertyReaderUtil.INSTANCE.getProperty("path_to_json"));
    }
}