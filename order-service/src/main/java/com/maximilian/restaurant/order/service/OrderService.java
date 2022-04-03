package com.maximilian.restaurant.order.service;

import com.maximilian.restaurant.client.CustomerClient;
import com.maximilian.restaurant.order.entity.Item;
import com.maximilian.restaurant.order.entity.Order;
import com.maximilian.restaurant.order.entity.OrderState;
import com.maximilian.restaurant.order.repository.ItemRepository;
import com.maximilian.restaurant.order.repository.OrderItemRepository;
import com.maximilian.restaurant.order.repository.OrderRepository;
import com.maximilian.restaurant.order.service.transaction.NamedAction;
import com.maximilian.restaurant.order.service.transaction.RevertableAction;
import com.maximilian.restaurant.order.service.transaction.RevertableActionsService;
import com.maximilian.restaurant.request.OrderRequest;
import com.maximilian.restaurant.response.CustomerResponse;
import com.maximilian.restaurant.response.OrderCreatedResponse;
import com.maximilian.restaurant.response.OrderResponse;
import com.maximilian.restaurant.rest.exception.GeneralException;
import com.maximilian.restaurant.service.BaseLoggableService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OrderService extends BaseLoggableService {

    private final OrderRepository orderRepository;
    // items or menu positions need to be stored in kitchen-service
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final Validator validator;
    private final RevertableActionsService revertableActionsService;
    private final CustomerClient customerClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ItemRepository itemRepository, Validator validator, RevertableActionsService revertableActionsService, CustomerClient customerClient) {
        super(LoggerFactory.getLogger(OrderService.class));
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.validator = validator;
        this.revertableActionsService = revertableActionsService;
        this.customerClient = customerClient;
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

    public OrderCreatedResponse createOrder(OrderRequest request) {
        logger.info("Got request: " + request);

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setOrderState(OrderState.WAITING_FOR_APPROVAL);
        Set<ConstraintViolation<Order>> violations = getViolations(order, validator);
        if (violations.isEmpty()) {
            order = orderRepository.saveAndFlush(order);
            revertableActionsService.performActions(getCreateOrderTransactionList(order));
            logger.info("Saved order " + order);
            return new OrderCreatedResponse(order.getId(), order.getOrderState().toString());
        } else {
            throw new GeneralException(getErrorMessagesTotal(violations));
        }
    }

    protected List<RevertableAction> getCreateOrderTransactionList(Order order) {
        List<RevertableAction> result = new ArrayList<>();

        // 2 create ticket in kitchen in status waiting for approval
        // 3 authorize card - action after which no rollbacks can be done - "final" action, reverts cannot be done if this completed normally
        // 4 set ticket status to approved

        // 1 check customer (and block customer for update until order is being created)
        result.add(new RevertableAction() {
            @Override
            public NamedAction getNamedAction() {
                return new NamedAction("add-customer-to-transaction-" + order.getId(), () -> {
                    CustomerResponse customer = customerClient.getCustomerAndBlockUpdate(order.getCustomerId());
                    logger.info("Got " + customer);
                    if(!customer.getCanMakeOrders()) {
                        throw new RuntimeException("Cannot create order for this customer");
                    }
                });
            }

            @Override
            public NamedAction getRollbackAction() {
                return new NamedAction("remove-customer-from-transaction-" + order.getId(), () -> {
                    customerClient.getCustomerAndUnblockUpdate(order.getCustomerId());
                });
            }
        });

        // 5 update customer status so customer can be updated
        result.add(new RevertableAction() {
            @Override
            public NamedAction getNamedAction() {
                return new NamedAction("remove-customer-from-transaction-" + order.getId(), () -> {
                    customerClient.getCustomerAndUnblockUpdate(order.getCustomerId());
                });
            }

            @Override
            public NamedAction getRollbackAction() {
                return new NamedAction("remove-customer-from-transaction-" + order.getId(), () -> {
                });
            }
        });

        // 6 set status of order to approved
        result.add(new RevertableAction() {
            @Override
            public NamedAction getNamedAction() {
                return new NamedAction("create-order-" + order.getId(), () -> {
                    setOrderState(order.getId(), OrderState.APPROVED);
                });
            }

            @Override
            public NamedAction getRollbackAction() {
                return new NamedAction("reject-order-" + order.getId(), () -> {
                    setOrderState(order.getId(), OrderState.REJECTED);
                });
            }
        });
        return result;
    }

    protected void setOrderState(Long id, OrderState state) {
        Order orderToUpdate = getOrderById(id);
        orderToUpdate.setOrderState(state);

        Set<ConstraintViolation<Order>> violations = getViolations(orderToUpdate, validator);
        if (violations.isEmpty()) {
            orderRepository.save(orderToUpdate);
        } else {
            throw new GeneralException(getErrorMessagesTotal(violations));
        }
    }

    public OrderResponse getOrderResponseById(Long id) {
        Order order = getOrderById(id);
        //todo about what should be returned
        return null;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Order with id #" + id + " not found"));
    }

}
