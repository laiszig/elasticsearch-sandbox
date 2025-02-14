package com.laiszig.springkafkaelasticsearch.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.laiszig.springkafkaelasticsearch.entity.Document;
import com.laiszig.springkafkaelasticsearch.exceptions.DocumentNotFoundException;
import com.laiszig.springkafkaelasticsearch.producer.DocumentProducer;
import com.laiszig.springkafkaelasticsearch.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentProducer documentProducer;

    @Autowired
    public DocumentController(DocumentService documentService, DocumentProducer documentProducer) {
        this.documentService = documentService;
        this.documentProducer = documentProducer;
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable String id) throws IOException {
        try {
            Document document = documentService.getDocument(id);
            return new ResponseEntity<>(document, HttpStatus.OK);
        } catch (DocumentNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/documents")
    public List<Document> getAllDocuments() {
        return documentService.getDocuments();
    }

    @PostMapping("/document")
    public String sendDocument(@RequestBody Document document) {
        documentProducer.sendDocument(document);
        return "Document sent to Kafka!";
    }


}
