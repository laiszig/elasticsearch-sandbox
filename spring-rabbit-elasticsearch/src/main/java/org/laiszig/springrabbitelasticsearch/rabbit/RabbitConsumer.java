package org.laiszig.springrabbitelasticsearch.rabbit;

import org.laiszig.springrabbitelasticsearch.service.DocumentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitConsumer {

    private DocumentService documentService;

    public RabbitConsumer(DocumentService documentService) {
        this.documentService = documentService;
    }

    @RabbitListener(queues = "doc-queue")
    public void receive(String content) throws IOException {
        documentService.upsertDocument(content);
    }
}
