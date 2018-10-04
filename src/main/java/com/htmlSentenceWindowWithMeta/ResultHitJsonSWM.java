package com.htmlSentenceWindowWithMeta;

import java.util.List;
import java.util.Map;

public class ResultHitJsonSWM {
    String fileName;
    String title;
    String description;
    String sentence;
    long number;
    String head;
    String label;
    String tail;
    float score;

    public ResultHitJsonSWM(Map<String, Object> sentenseWindowWithMeta, List<String> entities, float score) {
        fileName = (String) sentenseWindowWithMeta.get("fileName");
        sentence = (String) sentenseWindowWithMeta.get("sentence");
        number = Long.parseLong(sentenseWindowWithMeta.get("number").toString());
        description = (String) sentenseWindowWithMeta.get("description");
        title = (String) sentenseWindowWithMeta.get("title");
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

    public ResultHitJsonSWM(ResultHitJsonSWM sw) {
        fileName = sw.fileName;
        sentence = sw.sentence;
        number = sw.number;
        description = sw.description;
        title = sw.title;
        this.score = sw.score;
        head = sw.head;
        label = sw.label;
        tail = sw.tail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
