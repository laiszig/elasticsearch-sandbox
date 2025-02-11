package org.laiszig.springrabbitelasticsearch.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    public static final String MAIN_QUEUE = "elasticsearch.queue";
    public static final String DLQ_QUEUE = "elasticsearch.dlq";

    // Exchanges set in RabbitMQ manually
    public static final String EXCHANGE = "elasticsearch.exchange";
    public static final String DLX_EXCHANGE = "elasticsearch.dlx.exchange";

    @Bean
    Queue mainQueue() {
        return QueueBuilder.durable(MAIN_QUEUE)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(DLQ_QUEUE)
                .build();
    }

    @Bean
    Queue dlqQueue() {
        return QueueBuilder.durable(DLQ_QUEUE)
                .ttl(6000)
                .deadLetterExchange(EXCHANGE)
                .deadLetterRoutingKey(MAIN_QUEUE)
                .build();
    }

    @Bean
    public SimplePropertyValueConnectionNameStrategy cns() {
        return new SimplePropertyValueConnectionNameStrategy("spring.application.name");
    }

    @Bean
    public ConnectionFactory rabbitConnectionFactory(ConnectionNameStrategy cns) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setConnectionNameStrategy(cns);
        return connectionFactory;
    }
}
