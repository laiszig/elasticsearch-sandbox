package com.laiszig.simpleendpoints;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ElasticsearchHandler {
    private static final String ELASTICSEARCH_URL = "http://localhost:9200/my_index/_doc/";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public void handleCreate(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ELASTICSEARCH_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        sendElasticsearchRequest(exchange, request);
    }

    public void handleRead(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String url = query != null && query.contains("id=")
                ? ELASTICSEARCH_URL + query.split("=")[1]
                : "http://localhost:9200/my_index/_search?pretty";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        sendElasticsearchRequest(exchange, request);
    }

    public void handleUpdate(HttpExchange exchange) throws IOException {
        if (!"PUT".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.contains("id=")) {
            sendResponse(exchange, 400, "Missing document ID in query");
            return;
        }

        String id = query.split("=")[1];
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ELASTICSEARCH_URL + id))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        sendElasticsearchRequest(exchange, request);
    }

    public void handleDelete(HttpExchange exchange) throws IOException {
        if (!"DELETE".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.contains("id=")) {
            sendResponse(exchange, 400, "Missing document ID in query");
            return;
        }

        String id = query.split("=")[1];
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ELASTICSEARCH_URL + id))
                .DELETE()
                .build();

        sendElasticsearchRequest(exchange, request);
    }

    private void sendElasticsearchRequest(HttpExchange exchange, HttpRequest request) throws IOException {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            sendResponse(exchange, response.statusCode(), response.body());
        } catch (Exception e) {
            sendResponse(exchange, 500, "Error connecting to Elasticsearch: " + e.getMessage());
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}




