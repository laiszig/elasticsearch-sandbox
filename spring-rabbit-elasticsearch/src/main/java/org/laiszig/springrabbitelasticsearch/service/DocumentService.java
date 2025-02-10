package org.laiszig.springrabbitelasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.laiszig.springrabbitelasticsearch.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private ElasticsearchClient esClient;
    private ObjectMapper mapper;

    @Autowired
    public DocumentService(ElasticsearchClient esClient, ObjectMapper mapper) {
        this.esClient = esClient;
        this.mapper = mapper;
    }

    public void upsertDocument(String content) throws IOException {
        Document newDoc = mapper.readValue(content, Document.class);
        UpdateResponse<Document> updateResponse = esClient.update(u -> u
                .index("documents")
                .id(newDoc.id())
                .doc(newDoc)
                .docAsUpsert(true), Document.class);
        System.out.println(updateResponse.result());
    }

    public List<Document> getDocuments() {
        try {
            SearchResponse<Document> response = esClient.search(s -> s
                            .index("documents")
                            .query(q -> q.matchAll(m -> m)),
                    Document.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public Document findDocument(String id) throws IOException {
        GetResponse<Document> response = esClient.get(g -> g
                .index("documents")
                .id(id), Document.class);

        if (response.found()) {
            System.out.println("Document name " + response.source().name());
            return response.source();
        } else {
            System.out.println("Document not found");
            throw new NoSuchElementException();
        }
    }

}
