package com.laiszig.springkafkaelasticsearch.entity;

public record DocumentDTO(String id, String name, int age, String email) {
    public Document toDocument() {
        return new Document(id, name, age, email);
    }
}