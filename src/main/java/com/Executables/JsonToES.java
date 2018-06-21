package com.Executables;

import com.esutil.PropertyReaderUtil;
import com.htmlNestedObjects.HtmlPreHandle;
import com.htmlThreeSentenses.HtmlThreeSentencePreHandle;
import com.htmlparsershallow.HtmlShallowPreHandle;

public final class JsonToES {

    private JsonToES() {
    }

    public static void main(String[] args) {
        HtmlThreeSentencePreHandle htmlHandler = new HtmlThreeSentencePreHandle();
        htmlHandler.parceAllJsonToES(PropertyReaderUtil.INSTANCE.getProperty("path_to_json"));
    }
}