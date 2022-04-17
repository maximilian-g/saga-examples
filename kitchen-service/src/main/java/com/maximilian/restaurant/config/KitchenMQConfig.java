package com.maximilian.restaurant.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KitchenMQConfig {

    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queue.customer.valid}")
    private String customerValidQueue;

    @Value("${rabbitmq.queue.ticket.rejected}")
    private String ticketRejectQueue;

    @Value("${rabbitmq.queue.ticket.approved}")
    private String ticketApproveQueue;

    @Value("${rabbitmq.routing-keys.internal-customer-valid}")
    private String internalCustomerValidRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-card-auth}")
    private String internalCardAuthRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-ticket-approve}")
    private String internalTicketApproveRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-ticket-reject}")
    private String internalTicketRejectRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue customerValidQueue() {
        return new Queue(customerValidQueue);
    }

    @Bean
    public Queue ticketRejectQueue() {
        return new Queue(ticketRejectQueue);
    }

    @Bean
    public Queue ticketApproveQueue() {
        return new Queue(ticketApproveQueue);
    }

    @Bean
    public Binding internalToCustomerValidBinding() {
        return BindingBuilder
                .bind(customerValidQueue())
                .to(internalTopicExchange())
                .with(internalCustomerValidRoutingKey);
    }

    @Bean
    public Binding internalToTicketRejectBinding() {
        return BindingBuilder
                .bind(ticketRejectQueue())
                .to(internalTopicExchange())
                .with(internalTicketRejectRoutingKey);
    }

    @Bean
    public Binding internalToTicketApproveBinding() {
        return BindingBuilder
                .bind(ticketApproveQueue())
                .to(internalTopicExchange())
                .with(internalTicketApproveRoutingKey);
    }

    public String getInternalExchange() {
        return internalExchange;
    }

    public String getCustomerValidQueue() {
        return customerValidQueue;
    }

    public String getTicketRejectQueue() {
        return ticketRejectQueue;
    }

    public String getTicketApproveQueue() {
        return ticketApproveQueue;
    }

    public String getInternalCustomerValidRoutingKey() {
        return internalCustomerValidRoutingKey;
    }

    public String getInternalCardAuthRoutingKey() {
        return internalCardAuthRoutingKey;
    }

    public String getInternalTicketApproveRoutingKey() {
        return internalTicketApproveRoutingKey;
    }

    public String getInternalTicketRejectRoutingKey() {
        return internalTicketRejectRoutingKey;
    }
}
