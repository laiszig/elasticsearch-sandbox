package com.laiszig.simpleendpoints;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        ElasticsearchHandler handler = new ElasticsearchHandler();

        server.createContext("/create", handler::handleCreate);
        server.createContext("/read", handler::handleRead);
        server.createContext("/update", handler::handleUpdate);
        server.createContext("/delete", handler::handleDelete);

        server.setExecutor(null);
        server.start();
        System.out.println("Server is running on port 8080...");
    }
}

/*
Create document: http://localhost:8080/create
{
  "id": "1",
  "name": "John Doe",
  "age": 30
}
Read specific document: http://localhost:8080/read?id=1

Update document: http://localhost:8080/update
{
  "id": "1",
  "name": "Jane Doe",
  "age": 28
}

Delete document: http://localhost:8080/delete?id=1
 */
