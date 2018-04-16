package com.htmlparser;

public final class SandBox {

    private SandBox() {
    }

    public static void main(String[] args) {
        HtmlParser htmlParser = new HtmlParser();
        htmlParser.parceAllHtml();
        int i = 0;
    }


}