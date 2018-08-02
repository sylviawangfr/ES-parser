package com.htmlSentenceWindowWithMeta;

import java.util.List;
import java.util.Map;

public class ResultHitJsonSWM {
    String fileName;
    String title;
    String description;
    String sentence;
    long number;
    String entity1;
    String entity2;
    String entity3;
    float score;

    public ResultHitJsonSWM(Map<String, Object> sentenseWindowWithMeta, List<String> entities, float score) {
        fileName = (String) sentenseWindowWithMeta.get("fileName");
        sentence = (String) sentenseWindowWithMeta.get("sentence");
        number = Long.parseLong(sentenseWindowWithMeta.get("number").toString());
        description = (String) sentenseWindowWithMeta.get("description");
        title = (String) sentenseWindowWithMeta.get("title");
        this.score = score;
        if (entities.size() > 0) {
            entity1 = entities.get(0);
        }

        if (entities.size() > 1) {
            entity2 = entities.get(1);
        }

        if (entities.size() > 2) {
            entity3 = entities.get(2);
        }
    }

    public ResultHitJsonSWM(ResultHitJsonSWM sw) {
        fileName = sw.fileName;
        sentence = sw.sentence;
        number = sw.number;
        description = sw.description;
        title = sw.title;
        this.score = sw.score;
        entity1 = sw.entity1;
        entity2 = sw.entity2;
        entity3 = sw.entity3;
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

    public String getEntity2() {
        return entity2;
    }

    public void setEntity2(String entity2) {
        this.entity2 = entity2;
    }

    public String getEntity3() {
        return entity3;
    }

    public void setEntity3(String entity3) {
        this.entity3 = entity3;
    }

    public String getEntity1() {
        return entity1;
    }

    public void setEntity1(String entity1) {
        this.entity1 = entity1;
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
