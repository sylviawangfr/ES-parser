package com.htmlparser;

import com.esutil.ESEngine;
import com.esutil.ESSetter;
import com.esutil.SentenseSpliter;
import com.htmlparsershallow.HtmlParserShallow;

public final class SandBox {

    private SandBox() {
    }

    public static void main(String[] args) {

        HtmlParserShallow htmlParserShallow = new HtmlParserShallow();
        htmlParserShallow.parceAllHtml();
//        HtmlParser htmlParser = new HtmlParser();
//        htmlParser.parceAllHtml();
//        ESSetter esSetter = new ESSetter();
//        esSetter.putDocBulk();
//        ESEngine engine = new ESEngine();
//        engine.tryQuery();
//        SentenseSpliter ss = new SentenseSpliter();
//        ss.split("This is sentense1, this is part 2. This is sentense2, this is part3?");
    }


}