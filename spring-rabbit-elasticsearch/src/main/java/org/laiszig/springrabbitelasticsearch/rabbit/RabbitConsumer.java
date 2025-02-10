package org.laiszig.springrabbitelasticsearch.rabbit;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.laiszig.springrabbitelasticsearch.Document;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitConsumer {

    private ElasticsearchClient esClient;
    private ObjectMapper mapper;

    @Autowired
    public RabbitConsumer(ElasticsearchClient esClient, ObjectMapper mapper) {
        this.esClient = esClient;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "doc-queue")
    public void receive(String content) throws IOException {

        Document newDoc = mapper.readValue(content, Document.class);
        esClient.index(i -> i
                .index("documents")
                .id(newDoc.id())
                .document(newDoc));
        System.out.println(content);
    }
}
