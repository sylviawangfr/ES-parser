package com.Executables;

import com.esutil.PropertyReaderUtil;
import com.htmlNestedObjects.HtmlPreHandle;
import com.htmlparsershallow.HtmlShallowPreHandle;

public final class HtmlToES {

    private HtmlToES() {
    }

    public static void main(String[] args) {
        HtmlPreHandle htmlHandler = new HtmlPreHandle();
        htmlHandler.parceAllHtmlToES(PropertyReaderUtil.INSTANCE.getProperty("path_to_html"));
    }


}