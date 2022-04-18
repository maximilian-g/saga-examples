package com.maximilian.restaurant.order.service;

import com.maximilian.restaurant.amqp.RabbitMQMessageProducer;
import com.maximilian.restaurant.client.CustomerClient;
import com.maximilian.restaurant.client.KitchenClient;
import com.maximilian.restaurant.event.OrderCreated;
import com.maximilian.restaurant.order.config.OrderMQConfig;
import com.maximilian.restaurant.order.entity.Order;
import com.maximilian.restaurant.order.entity.OrderState;
import com.maximilian.restaurant.order.repository.OrderRepository;
import com.maximilian.restaurant.request.order.OrderRequest;
import com.maximilian.restaurant.response.order.OrderCreatedResponse;
import com.maximilian.restaurant.response.order.OrderResponse;
import com.maximilian.restaurant.rest.exception.GeneralException;
import com.maximilian.restaurant.service.BaseLoggableService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;

@Service
@Transactional
public class OrderService extends BaseLoggableService {

    private final OrderRepository orderRepository;
    private final Validator validator;
    private final RabbitMQMessageProducer producer;
    private final OrderMQConfig orderMQConfig;

    private final CustomerClient customerClient;
    private final KitchenClient kitchenClient;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        Validator validator,
                        RabbitMQMessageProducer producer,
                        OrderMQConfig orderMQConfig,
                        CustomerClient customerClient,
                        KitchenClient kitchenClient) {
        super(LoggerFactory.getLogger(OrderService.class));
        this.orderRepository = orderRepository;
        this.validator = validator;
        this.producer = producer;
        this.orderMQConfig = orderMQConfig;
        this.customerClient = customerClient;
        this.kitchenClient = kitchenClient;
    }

    public OrderCreatedResponse createOrder(OrderRequest request) {
        logger.info("Got request: " + request);

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setOrderState(OrderState.WAITING_FOR_APPROVAL);
        order.setDescription("Order is waiting for approval");

        validate(order, validator);

        order = orderRepository.saveAndFlush(order);

        producer.publish(new OrderCreated(request, order.getId()),
                orderMQConfig.getInternalExchange(),
                orderMQConfig.getInternalOrderCreatedRoutingKey());

        logger.info("Saved order " + order);
        return new OrderCreatedResponse(order.getId(), order.getOrderState().toString());
    }

    protected void setOrderState(Long id, OrderState state, String description) {
        Order orderToUpdate = getOrderById(id);
        orderToUpdate.setOrderState(state);
        orderToUpdate.setDescription(description);

        validate(orderToUpdate, validator);

        orderRepository.save(orderToUpdate);
    }

    public OrderResponse getOrderResponseById(Long id) {
        Order order = getOrderById(id);
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setStatus(order.getOrderState().toString());
        response.setDescription(order.getDescription());

        if (order.getOrderState() == OrderState.APPROVED) {
            response.setCustomer(customerClient.getCustomer(order.getCustomerId()));
            response.setTicket(kitchenClient.getKitchenTicket(order.getId()));
        }

        return response;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Order with id #" + id + " not found", HttpStatus.NOT_FOUND));
    }

}
