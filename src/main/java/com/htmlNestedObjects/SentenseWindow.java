package com.htmlNestedObjects;

import com.ValueTypes.Sentence;

public class SentenseWindow {


    String docId;
    Sentence[] sentences;
    String entity;

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public Sentence[] getSentences() {
        return sentences;
    }

    public void setSentences(Sentence[] sentences) {
        this.sentences = sentences;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }


    public String getDocId() {
        return docId;
    }

}
