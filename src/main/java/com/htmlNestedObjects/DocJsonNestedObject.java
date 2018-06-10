package com.htmlNestedObjects;

import com.ValueTypes.MetaData;
import com.ValueTypes.RelatedLinks;
import com.ValueTypes.Sentence;

public class DocJsonNestedObject {
    MetaData builtinMeta;
    Sentence [] content;
    RelatedLinks related_links;
    String head;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public Sentence [] getContent() {
        return content;
    }

    public MetaData getBuiltinMeta() {
        return builtinMeta;
    }

    public RelatedLinks getRelated_links() {
        return related_links;
    }

    public void setContent(Sentence [] body) {
        this.content = body;
    }

    public void setBuiltinMeta(MetaData meta) {
        this.builtinMeta = meta;
    }

    public void setRelated_links(RelatedLinks related_links) {
        this.related_links = related_links;
    }
}
