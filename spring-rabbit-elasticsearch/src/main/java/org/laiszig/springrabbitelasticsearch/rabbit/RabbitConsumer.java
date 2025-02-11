package org.laiszig.springrabbitelasticsearch.rabbit;

import org.laiszig.springrabbitelasticsearch.service.DocumentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

//TODO: Create custom exceptions
@Component
public class RabbitConsumer {

    private DocumentService documentService;

    public RabbitConsumer(DocumentService documentService) {
        this.documentService = documentService;
    }

    @RabbitListener(queues = RabbitConfig.MAIN_QUEUE)
    public void receive(String content) throws IOException {
        try {
            documentService.upsertDocument(content);
        } catch (Exception e) {
            System.err.println("Elasticsearch is down, moving message to DLQ");
            throw new RuntimeException("Failed to process message, retrying later...");
        }
    }


}
