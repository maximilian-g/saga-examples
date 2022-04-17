package com.maximilian.restaurant.service;

import com.maximilian.restaurant.event.OrderCreated;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CardAuthConsumer {

    private final CardAuthService cardAuthService;

    @Autowired
    public CardAuthConsumer(CardAuthService cardAuthService) {
        this.cardAuthService = cardAuthService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.card.auth}")
    public void consumeCreated(OrderCreated orderEvent) {
        cardAuthService.authorizeCard(orderEvent);
    }

}
