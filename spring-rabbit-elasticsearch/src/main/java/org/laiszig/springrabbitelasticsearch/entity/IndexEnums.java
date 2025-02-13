package org.laiszig.springrabbitelasticsearch.entity;

public enum IndexEnums {

    DOCUMENTS("Documents");

    private final String name;

    IndexEnums(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
