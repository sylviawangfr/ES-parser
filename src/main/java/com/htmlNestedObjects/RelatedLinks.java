package com.htmlNestedObjects;

import com.htmlNestedObjects.Link;

public class RelatedLinks {
    Link[] parentlinks;
    Link[] relatedInfo;
    Link[] childlinks;

    public Link[] getChildlinks() {
        return childlinks;
    }

    public Link[] getParentlinks() {
        return parentlinks;
    }

    public Link[] getRelatedInfo() {
        return relatedInfo;
    }

    public void setChildlinks(Link[] childlinks) {
        this.childlinks = childlinks;
    }

    public void setParentlinks(Link[] parentlinks) {
        this.parentlinks = parentlinks;
    }

    public void setRelatedInfo(Link[] relatedInfo) {
        this.relatedInfo = relatedInfo;
    }
}
