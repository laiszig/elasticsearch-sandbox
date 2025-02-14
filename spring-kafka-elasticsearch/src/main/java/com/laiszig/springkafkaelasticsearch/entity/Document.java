package com.laiszig.springkafkaelasticsearch.entity;

public record Document(String id, String name, Integer age, String email) {
    public Document withUpdatedFields(String name, Integer age, String email) {
        return new Document(id,
                name != null ? name : this.name,
                age != null ? age : this.age,
                email != null ? email : this.email);
    }
}