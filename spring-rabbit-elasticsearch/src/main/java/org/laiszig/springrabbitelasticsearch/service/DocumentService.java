package org.laiszig.springrabbitelasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.laiszig.springrabbitelasticsearch.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// TODO: Create custom exceptions
@Service
public class DocumentService {

    private ElasticsearchClient esClient;
    private ObjectMapper mapper;
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Autowired
    public DocumentService(ElasticsearchClient esClient, ObjectMapper mapper) {
        this.esClient = esClient;
        this.mapper = mapper;
    }

    public void upsertDocument(String content) throws IOException {
        Document newDoc = mapper.readValue(content, Document.class);
        performValidations(newDoc);

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

    private static void performValidations(Document newDoc) {
        validateId(newDoc);
        validateName(newDoc);
        validateAge(newDoc);
        validateEmail(newDoc);
    }

    private static void validateId(Document newDoc) {
        if (newDoc.id() == null || newDoc.id().isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
    }

    private static void validateName(Document newDoc) {
        String nameRegex = "^[A-Za-z ]+$";
        if (newDoc.name() == null || newDoc.name().isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (!newDoc.name().matches(nameRegex)) {
            throw new IllegalArgumentException("Name must contain only letters and spaces: " + newDoc.name());
        }
    }

    private static void validateAge(Document newDoc) {
        if (newDoc.age() < 18 || newDoc.age() > 120) {
            throw new IllegalArgumentException("Age must be between 0 and 150: " + newDoc.age());
        }
    }

    private static void validateEmail(Document newDoc) {
        if (!EMAIL_REGEX.matcher(newDoc.email()).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + newDoc.email());
        }
    }

}
