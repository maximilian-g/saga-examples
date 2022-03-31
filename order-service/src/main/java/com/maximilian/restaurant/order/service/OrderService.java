package com.maximilian.restaurant.order.service;

import com.maximilian.restaurant.order.entity.Item;
import com.maximilian.restaurant.order.entity.Order;
import com.maximilian.restaurant.order.entity.OrderItem;
import com.maximilian.restaurant.order.repository.ItemRepository;
import com.maximilian.restaurant.order.repository.OrderItemRepository;
import com.maximilian.restaurant.order.repository.OrderRepository;
import com.maximilian.restaurant.request.OrderRequest;
import com.maximilian.restaurant.response.OrderResponse;
import com.maximilian.restaurant.rest.exception.GeneralException;
import com.maximilian.restaurant.service.BaseLoggableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService extends BaseLoggableService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final Validator validator;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ItemRepository itemRepository, Validator validator) {
        super(LoggerFactory.getLogger(OrderService.class));
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.validator = validator;
        initItems();
    }

    private void initItems() {
        Item item = new Item();
        item.setName("Milk");
        itemRepository.saveAndFlush(item);
        item = new Item();
        item.setName("Bread");
        itemRepository.saveAndFlush(item);
        item = new Item();
        item.setName("Coffee");
        itemRepository.saveAndFlush(item);
    }

    public void createOrder(OrderRequest request) {
        logger.info("Got request: " + request);

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order = orderRepository.saveAndFlush(order);

        Order finalOrder = order;
        //todo here must be call to KitchenService with all items ordered, and all items must be saved to its database, not order database
        order.setOrderItems(request.getItems().stream().map(oi -> {
            OrderItem orderItem = OrderItem.from(oi, finalOrder);
            orderItem = orderItemRepository.save(orderItem);
            return orderItem;
        }).collect(Collectors.toList()));

        Set<ConstraintViolation<Order>> violations = getViolations(order, validator);
        if (violations.isEmpty()) {
            orderRepository.saveAndFlush(order);
            logger.info("Saved order " + order);
        } else {
            throw new GeneralException(getErrorMessagesTotal(violations));
        }
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Order with id #" + id + " not found"));
        // todo think about
        return null;
    }

}
