package com.maximilian.restaurant.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerMQConfig {

    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queue.order.created}")
    private String orderCreatedQueue;

    @Value("${rabbitmq.queue.customer.end-transaction}")
    private String customerEndTransactionQueue;

    @Value("${rabbitmq.routing-keys.internal-order-created}")
    private String internalOrderCreatedRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-customer-valid}")
    private String internalCustomerValidRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-order-reject}")
    private String internalOrderRejectRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-customer-end-transaction}")
    private String internalCustomerEndTransactionRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(orderCreatedQueue);
    }

    @Bean
    public Queue customerEndTransactionQueue() {
        return new Queue(customerEndTransactionQueue);
    }

    @Bean
    public Binding internalToOrderCreatedBinding() {
        return BindingBuilder
                .bind(orderCreatedQueue())
                .to(internalTopicExchange())
                .with(internalOrderCreatedRoutingKey);
    }

    @Bean
    public Binding internalToCustomerEndTransactionBinding() {
        return BindingBuilder
                .bind(customerEndTransactionQueue())
                .to(internalTopicExchange())
                .with(internalCustomerEndTransactionRoutingKey);
    }

    public String getInternalExchange() {
        return internalExchange;
    }

    public String getInternalOrderCreatedRoutingKey() {
        return internalOrderCreatedRoutingKey;
    }

    public String getInternalOrderRejectRoutingKey() {
        return internalOrderRejectRoutingKey;
    }

    public String getInternalCustomerValidRoutingKey() {
        return internalCustomerValidRoutingKey;
    }

    public String getInternalCustomerEndTransactionRoutingKey() {
        return internalCustomerEndTransactionRoutingKey;
    }
}
