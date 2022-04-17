package com.maximilian.restaurant.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CardAuthMQConfig {

    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queue.card.auth}")
    private String cardAuthQueue;

    @Value("${rabbitmq.routing-keys.internal-card-auth}")
    private String internalCardAuthRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-order-reject}")
    private String internalOrderRejectRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-order-approve}")
    private String internalOrderApproveRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-ticket-reject}")
    private String internalTicketRejectRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-ticket-approve}")
    private String internalTicketApproveRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-customer-end-transaction}")
    private String internalCustomerEndTransactionRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue cardAuthQueue() {
        return new Queue(cardAuthQueue);
    }

    @Bean
    public Binding internalToCardAuthBinding() {
        return BindingBuilder
                .bind(cardAuthQueue())
                .to(internalTopicExchange())
                .with(internalCardAuthRoutingKey);
    }

    public String getInternalExchange() {
        return internalExchange;
    }

    public String getInternalCardAuthRoutingKey() {
        return internalCardAuthRoutingKey;
    }

    public String getInternalOrderRejectRoutingKey() {
        return internalOrderRejectRoutingKey;
    }

    public String getInternalOrderApproveRoutingKey() {
        return internalOrderApproveRoutingKey;
    }

    public String getInternalTicketRejectRoutingKey() {
        return internalTicketRejectRoutingKey;
    }

    public String getInternalTicketApproveRoutingKey() {
        return internalTicketApproveRoutingKey;
    }

    public String getInternalCustomerEndTransactionRoutingKey() {
        return internalCustomerEndTransactionRoutingKey;
    }
}
