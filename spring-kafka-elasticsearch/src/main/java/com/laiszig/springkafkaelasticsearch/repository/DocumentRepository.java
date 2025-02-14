package com.laiszig.springkafkaelasticsearch.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.laiszig.springkafkaelasticsearch.entity.Document;
import com.laiszig.springkafkaelasticsearch.exceptions.DocumentNotFoundException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentRepository {

    private final ElasticsearchClient esClient;

    public DocumentRepository(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    public UpdateResponse<Document> getUpsertResponse(Document newDoc) throws IOException {
        return esClient.update(u -> u
                .index("documents")
                .id(newDoc.id())
                .doc(newDoc)
                .docAsUpsert(true), Document.class);
    }

    public List<Document> findAllDocuments() throws IOException {
        SearchResponse<Document> response = esClient.search(s -> s
                        .index("documents")
                        .query(q -> q.matchAll(m -> m)),
                Document.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public Document findDocument(String id) throws IOException {
        GetResponse<Document> response = esClient.get(g -> g
                .index("documents")
                .id(id), Document.class);

        if (!response.found()) {
            throw new DocumentNotFoundException("Document not found.");
        }
        System.out.println("Document name " + response.source().name());
        return response.source();
    }
}
