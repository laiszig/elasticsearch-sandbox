package org.laiszig.springrabbitelasticsearch.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.laiszig.springrabbitelasticsearch.entity.Document;
import org.laiszig.springrabbitelasticsearch.exceptions.DocumentNotFoundException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.laiszig.springrabbitelasticsearch.entity.IndexEnums.DOCUMENTS;

@Component
public class DocumentRepository {

    private final ElasticsearchClient esClient;

    public DocumentRepository(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    public UpdateResponse<Document> getUpsertResponse(Document newDoc) throws IOException {
        return esClient.update(u -> u
                .index(DOCUMENTS.getName())
                .id(newDoc.id())
                .doc(newDoc)
                .docAsUpsert(true), Document.class);
    }

    public List<Document> findAllDocuments() throws IOException {
        SearchResponse<Document> response = esClient.search(s -> s
                        .index(DOCUMENTS.getName())
                        .query(q -> q.matchAll(m -> m)),
                Document.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public Document findDocument(String id) throws IOException {
        GetResponse<Document> response = esClient.get(g -> g
                .index(DOCUMENTS.getName())
                .id(id), Document.class);

        if (!response.found()) {
            throw new DocumentNotFoundException("Document not found.");
        }
        System.out.println("Document name " + response.source().name());
        return response.source();
    }
}
