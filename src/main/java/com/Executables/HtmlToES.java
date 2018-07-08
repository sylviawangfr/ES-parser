package com.Executables;

import com.ValueTypes.HtmlConvertor;
import com.esutil.PropertyReaderUtil;
import com.htmlSentenceWindow.HtmlPreHandleSW;
import com.htmlSentenceWindowWithMeta.HtmlPreHandleSWM;

public final class HtmlToES {

    private HtmlToES() {
    }

    public static void main(String[] args) {
        HtmlConvertor htmlHandler = new HtmlPreHandleSWM();
        htmlHandler.parseAllHtmlToES(PropertyReaderUtil.INSTANCE.getProperty("path_to_html"));
    }


}