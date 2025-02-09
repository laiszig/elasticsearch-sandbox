package org.laiszig.springrabbitelasticsearch.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer {

    @RabbitListener(queues = "doc-queue")
    public void receive(String content) {

        System.out.println(content);

    }
}
