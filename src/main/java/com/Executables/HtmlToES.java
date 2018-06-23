package com.Executables;

import com.ValueTypes.HtmlConvertor;
import com.esutil.PropertyReaderUtil;
import com.htmlThreeSentenses.HtmlThreeSentencePreHandle;

public final class HtmlToES {

    private HtmlToES() {
    }

    public static void main(String[] args) {
        HtmlConvertor htmlHandler = new HtmlThreeSentencePreHandle();
        htmlHandler.parseAllHtmlToES(PropertyReaderUtil.INSTANCE.getProperty("path_to_html"));
    }


}