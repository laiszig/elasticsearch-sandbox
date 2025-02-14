package com.laiszig.springkafkaelasticsearch.entity;

public record DocumentDTO(String id, String name, Integer age, String email) {
    public Document toDocument() {
        return new Document(id, name, age, email);
    }
}