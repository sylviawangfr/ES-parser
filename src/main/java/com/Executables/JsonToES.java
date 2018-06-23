package com.Executables;

import com.ValueTypes.HtmlConvertor;
import com.esutil.PropertyReaderUtil;
import com.htmlThreeSentenses.HtmlThreeSentencePreHandle;

public final class JsonToES {

    private JsonToES() {
    }

    public static void main(String[] args) {
        HtmlConvertor htmlHandler = new HtmlThreeSentencePreHandle();
        htmlHandler.parseAllJsonToES(PropertyReaderUtil.INSTANCE.getProperty("path_to_json"));
    }
}