package com.maximilian.restaurant.service;

import com.maximilian.restaurant.event.OrderCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class KitchenConsumer {

    private final KitchenTicketItemService kitchenTicketItemService;
    private final Logger logger = LoggerFactory.getLogger(KitchenConsumer.class);

    public KitchenConsumer(KitchenTicketItemService kitchenTicketItemService) {
        this.kitchenTicketItemService = kitchenTicketItemService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.ticket.approved}")
    public void consumeApprove(Long ticketId) {
        logger.info("Consumed APPROVE message from queue for id " + ticketId);
        kitchenTicketItemService.approveKitchenTicketConverted(ticketId);
    }

    @RabbitListener(queues = "${rabbitmq.queue.ticket.rejected}")
    public void consumeReject(Long ticketId) {
        logger.info("Consumed REJECT message from queue for id " + ticketId);
        kitchenTicketItemService.rejectKitchenTicketConverted(ticketId);
    }

    // listens to customer service, customer service will publish message only if customer is valid and can make orders
    @RabbitListener(queues = "${rabbitmq.queue.customer.valid}")
    public void consumeCreated(OrderCreated orderEvent) {
        logger.info("Consumed CREATED message from queue for order with id #" + orderEvent.getOrderId());
        kitchenTicketItemService.createKitchenTicket(orderEvent);
    }

}
