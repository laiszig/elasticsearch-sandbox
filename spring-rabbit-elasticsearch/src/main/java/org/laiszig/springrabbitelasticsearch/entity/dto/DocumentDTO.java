package org.laiszig.springrabbitelasticsearch.entity.dto;

import org.laiszig.springrabbitelasticsearch.entity.Document;

public record DocumentDTO(String id, String name, int age, String email) {
    public Document toDocument() {
        return new Document(id, name, age, email);
    }
}

