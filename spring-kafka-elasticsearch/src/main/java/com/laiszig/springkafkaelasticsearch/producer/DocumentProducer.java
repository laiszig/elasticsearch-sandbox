package com.laiszig.springkafkaelasticsearch.producer;

import com.laiszig.springkafkaelasticsearch.entity.Document;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DocumentProducer {
    private final KafkaTemplate<String, Document> kafkaTemplate;

    public DocumentProducer(KafkaTemplate<String, Document> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendDocument(Document document) {
        kafkaTemplate.send("documents", document.id(), document);
        System.out.println("Sent to Kafka: " + document);
    }

}
