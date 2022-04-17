package com.maximilian.restaurant.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderMQConfig {

    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queue.order.created}")
    private String orderCreatedQueue;

    @Value("${rabbitmq.queue.order.rejected}")
    private String orderRejectQueue;

    @Value("${rabbitmq.queue.order.approved}")
    private String orderApproveQueue;

    @Value("${rabbitmq.routing-keys.internal-order-created}")
    private String internalOrderCreatedRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-order-reject}")
    private String internalOrderRejectRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-order-approve}")
    private String internalOrderApproveRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(orderCreatedQueue);
    }

    @Bean
    public Queue orderRejectQueue() {
        return new Queue(orderRejectQueue);
    }

    @Bean
    public Queue orderApproveQueue() {
        return new Queue(orderApproveQueue);
    }

    @Bean
    public Binding internalToOrderCreatedBinding() {
        return BindingBuilder
                .bind(orderCreatedQueue())
                .to(internalTopicExchange())
                .with(internalOrderCreatedRoutingKey);
    }

    @Bean
    public Binding internalToOrderRejectBinding() {
        return BindingBuilder
                .bind(orderRejectQueue())
                .to(internalTopicExchange())
                .with(internalOrderRejectRoutingKey);
    }

    @Bean
    public Binding internalToOrderApproveBinding() {
        return BindingBuilder
                .bind(orderApproveQueue())
                .to(internalTopicExchange())
                .with(internalOrderApproveRoutingKey);
    }

    public String getInternalExchange() {
        return internalExchange;
    }

    public String getOrderCreatedQueue() {
        return orderCreatedQueue;
    }

    public String getOrderRejectQueue() {
        return orderRejectQueue;
    }

    public String getOrderApproveQueue() {
        return orderApproveQueue;
    }

    public String getInternalOrderCreatedRoutingKey() {
        return internalOrderCreatedRoutingKey;
    }

    public String getInternalOrderRejectRoutingKey() {
        return internalOrderRejectRoutingKey;
    }

    public String getInternalOrderApproveRoutingKey() {
        return internalOrderApproveRoutingKey;
    }
}
