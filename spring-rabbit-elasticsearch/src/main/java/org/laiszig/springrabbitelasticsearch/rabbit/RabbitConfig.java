package org.laiszig.springrabbitelasticsearch.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    public static final String MAIN_QUEUE = "elasticsearch.queue";
    public static final String DLQ_QUEUE = "elasticsearch.dlq";

    // Exchanges created to simulate publisher
    public static final String EXCHANGE = "elasticsearch.exchange";
    public static final String DLX_EXCHANGE = "elasticsearch.dlx.exchange";

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    Queue queue() {
        return QueueBuilder.durable(MAIN_QUEUE).withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "elasticDeadLetter").build();
    }

    @Bean
    Queue dlq() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("elasticsearch");
    }

    @Bean
    Binding DLQbinding() {
        return BindingBuilder.bind(dlq()).to(deadLetterExchange()).with("elasticDeadLetter");
    }

}
