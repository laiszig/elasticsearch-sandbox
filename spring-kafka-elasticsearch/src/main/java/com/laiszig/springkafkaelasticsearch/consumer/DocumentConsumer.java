package com.laiszig.springkafkaelasticsearch.consumer;

import com.laiszig.springkafkaelasticsearch.exceptions.InvalidInputException;
import com.laiszig.springkafkaelasticsearch.service.DocumentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DocumentConsumer {

    private final DocumentService documentService;

    public DocumentConsumer(DocumentService documentService) {
        this.documentService = documentService;
    }

    @KafkaListener(id = "doc-consumer", topics = "documents", clientIdPrefix = "myClientId")
    public void listen(String data) {
        try {
            documentService.upsertDocument(data);
        } catch (InvalidInputException | IllegalArgumentException e) {
            System.err.println("Validation error for document: " + e.getMessage() + ", moving message to DLQ");
        } catch (Exception e) {
            System.err.println("Elasticsearch is down, moving message to DLQ");
            throw new RuntimeException("Failed to process message");
        }
    }
}
