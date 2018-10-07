package com.dbpediaSentenseWindow;

import java.util.List;
import java.util.Map;

public class ResultHitJsonSWDBpedia {


    public String uri;
    public String derivedFrom;
    public String sentence;
    public long number;
    public String head;
    public String label;
    public String tail;
    public float score;

    public ResultHitJsonSWDBpedia(Map<String, Object> sentenseWindowDBpedia, List<String> entities, float score) {
        uri = (String) sentenseWindowDBpedia.get("uri");
        sentence = (String) sentenseWindowDBpedia.get("sentence");
        number = Long.parseLong(sentenseWindowDBpedia.get("number").toString());
        derivedFrom = (String) sentenseWindowDBpedia.get("derivedFrom");

        this.score = score;
        if (entities.size() > 0) {
            head = entities.get(0);
        }

        if (entities.size() > 1) {
            label = entities.get(1);
        }

        if (entities.size() > 2) {
            tail = entities.get(2);
        }
    }

    public ResultHitJsonSWDBpedia(DocJsonSWDBpedia sw, List<String> entities, float score) {
        uri = sw.uri;
        sentence = sw.sentence;
        number = sw.number;
        derivedFrom = sw.derivedFrom;

        this.score = score;
        if (entities.size() > 0) {
            head = entities.get(0);
        }

        if (entities.size() > 1) {
            label = entities.get(1);
        }

        if (entities.size() > 2) {
            tail = entities.get(2);
        }
    }

    public ResultHitJsonSWDBpedia(ResultHitJsonSWDBpedia sw) {
        uri = sw.uri;
        sentence = sw.sentence;
        number = sw.number;
        derivedFrom = sw.derivedFrom;

        this.score = sw.score;
        head = sw.head;
        label = sw.label;
        tail = sw.tail;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDerivedFrom() {
        return derivedFrom;
    }

    public void setDerivedFrom(String derivedFrom) {
        this.derivedFrom = derivedFrom;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTail() {
        return tail;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }
}
