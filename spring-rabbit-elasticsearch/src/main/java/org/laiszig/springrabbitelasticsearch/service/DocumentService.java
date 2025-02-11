package org.laiszig.springrabbitelasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.laiszig.springrabbitelasticsearch.Document;
import org.laiszig.springrabbitelasticsearch.exceptions.DocumentNotFoundException;
import org.laiszig.springrabbitelasticsearch.exceptions.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    public static final String DOCUMENTS = "documents";
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
                .index(DOCUMENTS)
                .id(newDoc.id())
                .doc(newDoc)
                .docAsUpsert(true), Document.class);
        System.out.println(updateResponse.result());
    }

    public List<Document> getDocuments() {
        try {
            SearchResponse<Document> response = esClient.search(s -> s
                            .index(DOCUMENTS)
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
                .index(DOCUMENTS)
                .id(id), Document.class);

        if (!response.found()) {
            throw new DocumentNotFoundException("Document not found.");
        }
        System.out.println("Document name " + response.source().name());
        return response.source();
    }

    private static void performValidations(Document newDoc) {
        validateId(newDoc);
        validateName(newDoc);
        validateAge(newDoc);
        validateEmail(newDoc);
    }

    private static void validateId(Document newDoc) {
        if (newDoc.id() == null || newDoc.id().isBlank()) {
            throw new InvalidInputException("ID cannot be null or empty");
        }
    }

    private static void validateName(Document newDoc) {
        String nameRegex = "^[A-Za-z ]+$";
        if (newDoc.name() == null || newDoc.name().isBlank()) {
            throw new InvalidInputException("Name cannot be null or empty");
        }
        if (!newDoc.name().matches(nameRegex)) {
            throw new InvalidInputException("Name must contain only letters and spaces: " + newDoc.name());
        }
    }

    private static void validateAge(Document newDoc) {
        if (newDoc.age() < 18 || newDoc.age() > 120) {
            throw new InvalidInputException("Age must be between 0 and 150: " + newDoc.age());
        }
    }

    private static void validateEmail(Document newDoc) {
        if (!EMAIL_REGEX.matcher(newDoc.email()).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + newDoc.email());
        }
    }

}
