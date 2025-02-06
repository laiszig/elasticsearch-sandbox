import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ElasticsearchCRUD {
    private static final String ELASTICSEARCH_URL = "http://localhost:9200/my_index/_doc/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        String documentId = "1";

        // CREATE
        createDocument(documentId, "John Doe", 30);

        // READ
        readDocument(documentId);

        // UPDATE
        updateDocument(documentId, "Jane Doe", 25);

        // DELETE
        deleteDocument(documentId);
    }

    public static void createDocument(String id, String name, int age) throws IOException {
        Map<String, Object> document = new HashMap<>();
        document.put("name", name);
        document.put("age", age);
        String json = objectMapper.writeValueAsString(document);

        executeRequest(new HttpPut(ELASTICSEARCH_URL + id), json, "Create");
    }

    public static void readDocument(String id) throws IOException {
        executeRequest(new HttpGet(ELASTICSEARCH_URL + id), null, "Read");
    }

    public static void updateDocument(String id, String name, int age) throws IOException {
        Map<String, Object> document = new HashMap<>();
        document.put("name", name);
        document.put("age", age);
        String json = objectMapper.writeValueAsString(document);

        executeRequest(new HttpPost(ELASTICSEARCH_URL + id + "/_update"), "{ \"doc\": " + json + " }", "Update");
    }

    public static void deleteDocument(String id) throws IOException {
        executeRequest(new HttpDelete(ELASTICSEARCH_URL + id), null, "Delete");
    }

    private static void executeRequest(HttpUriRequestBase request, String json, String operation) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            if (json != null) {
                request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            }
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                System.out.println(operation + " Response: " + response.getCode());
            }
        }
    }
}
