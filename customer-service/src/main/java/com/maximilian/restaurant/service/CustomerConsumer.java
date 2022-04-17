package com.maximilian.restaurant.service;

import com.maximilian.restaurant.event.OrderCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerConsumer {

    private final CustomerService customerService;
    private final Logger logger = LoggerFactory.getLogger(CustomerConsumer.class);

    @Autowired
    public CustomerConsumer(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.order.created}")
    public void consumeCreated(OrderCreated orderEvent) {
        logger.info("Consumed CREATED message from queue for order with id #" + orderEvent.getOrderId());
        customerService.startTransactionForCustomerByEvent(orderEvent);
    }

}
