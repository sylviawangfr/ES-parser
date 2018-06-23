package com.htmlSentenceWindow;

import java.util.List;
import java.util.Map;

public class ResultHitJsonSW {
    String fileName;
    String sentence;
    long number;
    String entity1;
    String entity2;
    String entity3;

    public ResultHitJsonSW(Map<String, String> sentenseWindow, List<String> entities) {
        fileName = sentenseWindow.get("fileName");
        sentence = sentenseWindow.get("sentence");
        number = Long.parseLong(sentenseWindow.get("number"));
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
