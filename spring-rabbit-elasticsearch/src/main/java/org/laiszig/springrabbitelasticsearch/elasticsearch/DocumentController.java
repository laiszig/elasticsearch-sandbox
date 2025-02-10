package org.laiszig.springrabbitelasticsearch.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.laiszig.springrabbitelasticsearch.Document;
import org.laiszig.springrabbitelasticsearch.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class DocumentController {

    private final ElasticsearchClient esClient;
    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService, ElasticsearchClient esClient) {
        this.documentService = documentService;
        this.esClient = esClient;
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable String id) throws IOException {
        try {
            Document document = documentService.findDocument(id);
            return new ResponseEntity<>(document, HttpStatus.OK);
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("/documents")
    public List<Document> getAllDocuments() {
        return documentService.getDocuments();
    }

    @PostMapping("/create-index")
    public ResponseEntity<String> createIndex(@RequestBody String index) throws IOException {
        esClient.indices()
                .create(c -> c.index(index));
        return ResponseEntity.ok("Index 'documents' created successfully.");
    }
}
