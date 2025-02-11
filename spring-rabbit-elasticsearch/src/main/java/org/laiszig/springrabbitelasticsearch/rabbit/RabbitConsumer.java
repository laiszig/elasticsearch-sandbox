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

    // TODO: Manual Ack, Deadletter-queue
    @RabbitListener(queues = "doc-queue")
    public void receive(String content) throws IOException {
        try {
            documentService.upsertDocument(content);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
