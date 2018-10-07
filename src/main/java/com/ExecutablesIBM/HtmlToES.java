package com.ExecutablesIBM;

import com.ValueTypes.HtmlConvertor;
import com.esutil.PropertyReaderUtil;
import com.htmlSentenceWindowWithMeta.HtmlToSW;

public final class HtmlToES {

    private HtmlToES() {
    }

    public static void main(String[] args) {
        HtmlConvertor htmlHandler = new HtmlToSW();
        htmlHandler.parseAllHtmlToES(PropertyReaderUtil.INSTANCE.getProperty("path_to_html"));
    }


}