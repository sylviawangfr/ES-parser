package com.htmlparser;

public class MetaData {
    String copyright;
    String dc_type;
    String title;
    String dc_abstract;
    String description;
    String subject;
    String keywords;
    String [] relations;
    String date;
    String identifier;
    String ibm_country;
    String language;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getDc_abstract() {
        return dc_abstract;
    }

    public String getDate() {
        return date;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getLanguage() {
        return language;
    }
    public String getSubject() {
        return subject;
    }

    public String getDc_type() {
        return dc_type;
    }

    public String getDescription() {
        return description;
    }

    public String getIbm_country() {
        return ibm_country;
    }

    public String getKeywords() {
        return keywords;
    }

    public String[] getRelations() {
        return relations;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public void setDc_abstract(String dc_abstract) {
        this.dc_abstract = dc_abstract;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setRelations(String[] relations) {
        this.relations = relations;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDc_type(String dc_type) {
        this.dc_type = dc_type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIbm_country(String ibm_country) {
        this.ibm_country = ibm_country;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

}
