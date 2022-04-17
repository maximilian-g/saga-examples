package com.maximilian.restaurant.service;

import com.maximilian.restaurant.amqp.RabbitMQMessageProducer;
import com.maximilian.restaurant.config.CardAuthMQConfig;
import com.maximilian.restaurant.data.CardDetails;
import com.maximilian.restaurant.event.OrderCreated;
import com.maximilian.restaurant.rest.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardAuthService {

    private final RabbitMQMessageProducer producer;
    private final Logger logger = LoggerFactory.getLogger(CardAuthService.class);
    private final CardAuthMQConfig cardAuthMQConfig;

    @Autowired
    public CardAuthService(RabbitMQMessageProducer producer, CardAuthMQConfig cardAuthMQConfig) {
        this.producer = producer;
        this.cardAuthMQConfig = cardAuthMQConfig;
    }

    public void authorizeCard(OrderCreated event) {
        logger.info("Got event for order #" + event.getOrderId());
        RestResponse response = authorizeCard(event.getCardDetails());
        if(response.isSuccess()) {
            // approving
            sendPayloadToInternalExchange(event.getOrderId(),
                    cardAuthMQConfig.getInternalTicketApproveRoutingKey());
            sendPayloadToInternalExchange(event.getOrderId(),
                    cardAuthMQConfig.getInternalOrderApproveRoutingKey());
        } else {
            // rejecting
            sendPayloadToInternalExchange(event.getOrderId(),
                    cardAuthMQConfig.getInternalTicketRejectRoutingKey());
            sendPayloadToInternalExchange(event.getOrderId(),
                    cardAuthMQConfig.getInternalOrderRejectRoutingKey());
        }
        // ending transaction for customer
        sendPayloadToInternalExchange(event.getCustomerId(),
                cardAuthMQConfig.getInternalCustomerEndTransactionRoutingKey());
    }

    public RestResponse authorizeCard(CardDetails details) {
        // mock of card authorization
        try {
            Thread.sleep((long)(5000 + (10000 * Math.random())));
        } catch (Exception ignored) {

        }
        RestResponse restResponse = new RestResponse();
        restResponse.setSuccess(Double.compare(Math.random(), 0.5) > 0);
        if(restResponse.isSuccess()) {
            logger.info("Authorization successful");
            restResponse.setMessage("Authorized card ending with *" + details.getCardNumber().substring(details.getCardNumber().length() - 4));
        } else {
            logger.info("Authorization failed");
            restResponse.setMessage("Failed to authorize card ending with *" + details.getCardNumber().substring(details.getCardNumber().length() - 4));
        }
        return restResponse;
    }

    private void sendPayloadToInternalExchange(Object payload, String routingKey) {
        producer.publish(payload,
                cardAuthMQConfig.getInternalExchange(),
                routingKey);
    }

}
