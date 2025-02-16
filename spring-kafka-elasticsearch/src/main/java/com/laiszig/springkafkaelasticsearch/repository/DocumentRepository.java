package com.laiszig.springkafkaelasticsearch.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.laiszig.springkafkaelasticsearch.entity.Document;
import com.laiszig.springkafkaelasticsearch.entity.DocumentWithMetadata;
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

    public void getUpsertResponse(DocumentWithMetadata metadata) throws IOException {
        System.out.println("Trying to update document of ID: " + metadata.document().id() + " / seqNo= " + metadata.seqNo() + "  / primary= " + metadata.primaryTerm());
        UpdateResponse<Document> response = esClient.update(u -> u
                        .index("documents")
                        .id(metadata.document().id())
                        .ifPrimaryTerm(metadata.primaryTerm())
                        .ifSeqNo(metadata.seqNo())
                        .doc(metadata.document()),
                Document.class
        );
        System.out.println("Response:  " + response.result());
    }

    public void createNewDocument(Document newDoc) throws IOException {
        IndexResponse response = esClient.index(i -> i
                .index("documents")
                .id(newDoc.id())
                .document(newDoc)
        );
        System.out.println("Response:  " + response.result());
    }

    public List<Document> findAllDocuments() throws IOException {
        SearchResponse<Document> response = esClient.search(s -> s
                        .index("documents")
                        .query(q -> q.matchAll(m -> m)),
                Document.class);
        System.out.println("Documents found:  " + response.hits());

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public GetResponse<Document> findDocument(String id) throws IOException {
        GetResponse<Document> response = esClient.get(g -> g
                .index("documents")
                .id(id), Document.class);

        if (!response.found()) {
            throw new DocumentNotFoundException("Document not found.");
        }
        System.out.println("Response:  " + response);
        return response;
    }
}
