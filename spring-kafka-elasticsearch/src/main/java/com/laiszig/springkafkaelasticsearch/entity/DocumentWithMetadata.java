package com.laiszig.springkafkaelasticsearch.entity;

public record DocumentWithMetadata (Document document, Long primaryTerm, Long seqNo){
}
