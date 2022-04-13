package com.maximilian.restaurant.order.service;

import com.maximilian.restaurant.order.entity.OrderState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageConsumer {

    private final OrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(OrderMessageConsumer.class);

    @Autowired
    public OrderMessageConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.order.approved}")
    public void consumeApprove(Long orderId) {
        logger.info("Consumed APPROVE message from queue for id " + orderId);
        orderService.setOrderState(orderId, OrderState.APPROVED);
    }

    @RabbitListener(queues = "${rabbitmq.queue.order.rejected}")
    public void consumeReject(Long orderId) {
        logger.info("Consumed REJECT message from queue for id " + orderId);
        orderService.setOrderState(orderId, OrderState.REJECTED);
    }

}
