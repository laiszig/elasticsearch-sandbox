package org.laiszig.springrabbitelasticsearch.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import org.laiszig.springrabbitelasticsearch.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@Controller
public class DocController {

    private final ElasticsearchClient esClient;

    @Autowired
    public DocController(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable String id) throws IOException {
        GetResponse<Document> response = esClient.get(g -> g
                        .index("documents")
                        .id(id), Document.class);

        if (!response.found()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(response.source());
    }

    @PostMapping("/documents")
    public ResponseEntity<String> indexDocument(@RequestBody Document newDoc) throws IOException {
        IndexResponse response = esClient.index(i -> i
                .index("documents")
                .id(newDoc.id())
                .document(newDoc));
        return ResponseEntity.ok("Document indexed with version " + response.version());
    }

    @PostMapping("/create-index")
    public ResponseEntity<String> createIndex() throws IOException {
        esClient.indices().create(c -> c.index("documents"));
        return ResponseEntity.ok("Index 'documents' created successfully.");
    }
}
