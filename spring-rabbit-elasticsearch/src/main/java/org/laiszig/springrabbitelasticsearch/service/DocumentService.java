package org.laiszig.springrabbitelasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.laiszig.springrabbitelasticsearch.entity.Document;
import org.laiszig.springrabbitelasticsearch.entity.dto.DocumentDTO;
import org.laiszig.springrabbitelasticsearch.exceptions.DocumentNotFoundException;
import org.laiszig.springrabbitelasticsearch.exceptions.InvalidInputException;
import org.laiszig.springrabbitelasticsearch.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ObjectMapper mapper;
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Autowired
    public DocumentService(DocumentRepository documentRepository, ObjectMapper mapper) {
        this.documentRepository = documentRepository;
        this.mapper = mapper;
    }

    public void upsertDocument(String content) throws IOException {
        DocumentDTO dto = mapper.readValue(content, DocumentDTO.class);
        Document newDoc = dto.toDocument();

        performValidations(newDoc);

        UpdateResponse<Document> updateResponse = documentRepository.getUpsertResponse(newDoc);
        System.out.println(updateResponse.result());
    }

    public List<Document> getDocuments() {
        try {
            return documentRepository.findAllDocuments();
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public Document getDocument(String id) throws IOException {
        return documentRepository.findDocument(id);
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
