package com.esutil;

import com.ValueTypes.Link;
import com.ValueTypes.MetaData;
import com.ValueTypes.RelatedLinks;
import com.ValueTypes.Sentence;
import com.esutil.PropertyReaderUtil;
import com.esutil.SentenseSpliter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;



public class HtmlParser {

    public Sentence[] parseBody(Document doc) {
        String body = doc.select("div[class^='body']").text();
        List<String > sentencesStr =  SentenseSpliter.split(body); //.toArray(new String[0]);
        List<Sentence> sentences = new ArrayList<>();
        int i = 0;
        for(String s : sentencesStr) {
            Sentence st = new Sentence();
            st.setSentence(s);
            st.setId(i);
            sentences.add(st);
            i++;
        }
        return sentences.toArray(new Sentence[0]);
    }

    public MetaData parseMeta(Document doc) {
        try {
            //get meta description content
            Elements metas = doc.select("meta");
            MetaData metaData = new MetaData();
            List<String> DC_Relation = new ArrayList<String>();
            for (Element e : metas) {
                String name = e.attr("name");
                String content = e.attr("content");
                switch (name) {
                    case "copyright":
                        metaData.setCopyright(content);
                    case "DC.Type":
                        metaData.setDc_type(content);
                        break;
                    case "DC.Title":
                        metaData.setTitle(content);
                        break;
                    case "DC.subject":
                        metaData.setSubject(content);
                        break;
                    case "Description":
                        metaData.setDescription(content);
                        break;
                    case "abstract":
                        metaData.setDc_abstract(content);
                        break;
                    case "keywords":
                        metaData.setKeywords(content);
                        break;
                    case "DC.Relation":
                        DC_Relation.add(content);
                        break;
                    case "DC.Date":
                        metaData.setDate(content);
                        break;
                    case "DC.Identifier":
                        metaData.setIdentifier(content);
                        break;
                    case "DC.Language":
                        metaData.setLanguage(content);
                        break;
                    case "IBM.Country":
                        metaData.setIbm_country(content);
                }
            }
            if(DC_Relation != null && DC_Relation.size() > 0) {
                metaData.setRelations(DC_Relation.toArray(new String[0]));
            }
            return metaData;
        } catch (Exception e){
            return null;
        }
    }

    public String parseHead(Document doc) {
        Elements head = doc.select("dochdr");
        Elements headCode = doc.select("w4tss_mtm");
        String headStr = "";
        for(Element e : head) {
            headStr = headStr + e.text() + " ";
        }
        for(Element e : headCode) {
            headStr = headStr + e.text() + " ";
        }

        return headStr.trim();
    }

    public List<Link> parseLinks(Element linkEle) {
        List<Link> objList = new ArrayList<>();
        Elements links = linkEle.select("a");
        for(Element e : links) {
            Link obj = new Link();
            obj.setHref(e.attr("href"));
            obj.setTitle(e.attr("title"));

            obj.setLink_text(e.text());
            objList.add(obj);
        }
        return objList;
    }

    public List<Link> getListLinks(Document doc) {
        Elements childLinkEles = doc.select("div.related-links > ul.ullinks > li");
        List<Link> objList = new ArrayList<>();
        for (Element e : childLinkEles) {
            Link obj = new Link();
            obj.setHref(e.getElementsByTag("a").attr("href"));
            obj.setTitle(e.getElementsByTag("a").text());
            obj.setLink_text(e.text());
            objList.add(obj);
        }
        return objList;
    }
    public RelatedLinks parseRelatedLinks(Document doc) {
        try {
            RelatedLinks relatedLinks = new RelatedLinks();
            List<Link> parentLinks = new ArrayList<>();
            Elements parentLinkEles = doc.getElementsByClass("parentlink");
            if(parentLinkEles != null && parentLinkEles.size() > 0) {
                for(Element e : parentLinkEles) {
                    parentLinks.addAll(parseLinks(e));
                }
            }

            List<Link> relInfoLinks = new ArrayList<>();
            Elements relinfo = doc.getElementsByClass("relinfo relconcepts");
            if(relinfo != null && relinfo.size() > 0) {
                for(Element e : relinfo) {
                    relInfoLinks.addAll(parseLinks(e));
                }
            }

            List<Link> childLinks = getListLinks(doc);

            relatedLinks.setChildlinks(childLinks.toArray(new Link[0]));
            relatedLinks.setParentlinks(parentLinks.toArray(new Link[0]));
            relatedLinks.setRelatedInfo(relInfoLinks.toArray(new Link[0]));


            return relatedLinks;
        } catch (Exception e){
            return null;
        }
    }

}
