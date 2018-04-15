package com.htmlparser;

public class DocJson {
    MetaData meta;
    String body;
    RelatedLinks related_links;
    String head;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getBody() {
        return body;
    }

    public MetaData getMeta() {
        return meta;
    }

    public RelatedLinks getRelated_links() {
        return related_links;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setMeta(MetaData meta) {
        this.meta = meta;
    }

    public void setRelated_links(RelatedLinks related_links) {
        this.related_links = related_links;
    }
}
