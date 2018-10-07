package com.ExecutablesIBM;

import com.ValueTypes.HtmlConvertor;
import com.esutil.PropertyReaderUtil;
import com.htmlSentenceWindow.HtmlPreHandleSW;

public final class JsonToES {

    private JsonToES() {
    }

    public static void main(String[] args) {
        HtmlConvertor htmlHandler = new HtmlPreHandleSW();
        htmlHandler.parseAllJsonToES(PropertyReaderUtil.INSTANCE.getProperty("path_to_json"));
    }
}