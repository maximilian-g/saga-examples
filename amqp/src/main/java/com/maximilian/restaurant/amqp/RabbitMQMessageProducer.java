package com.maximilian.restaurant.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQMessageProducer {

    private final AmqpTemplate amqpTemplate;
    private final Logger logger = LoggerFactory.getLogger(RabbitMQMessageProducer.class);

    @Autowired
    public RabbitMQMessageProducer(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void publish (Object payload, String exchange, String routingKey) {
        logger.info("Sending message to exchange {} with routing key {} and payload {}", exchange, routingKey, payload);
        amqpTemplate.convertAndSend(exchange, routingKey, payload);
        logger.info("Sent message to exchange {} with routing key {} and payload {}", exchange, routingKey, payload);
    }

}