package com.maximilian.restaurant.order.service;

import com.maximilian.restaurant.client.CustomerClient;
import com.maximilian.restaurant.client.KitchenClient;
import com.maximilian.restaurant.order.entity.Item;
import com.maximilian.restaurant.order.entity.Order;
import com.maximilian.restaurant.order.entity.OrderState;
import com.maximilian.restaurant.order.repository.ItemRepository;
import com.maximilian.restaurant.order.repository.OrderItemRepository;
import com.maximilian.restaurant.order.repository.OrderRepository;
import com.maximilian.restaurant.request.kitchen.KitchenOrderRequest;
import com.maximilian.restaurant.request.order.OrderRequest;
import com.maximilian.restaurant.response.customer.CustomerResponse;
import com.maximilian.restaurant.response.kitchen.KitchenOrderResponse;
import com.maximilian.restaurant.response.order.OrderCreatedResponse;
import com.maximilian.restaurant.response.order.OrderResponse;
import com.maximilian.restaurant.rest.exception.GeneralException;
import com.maximilian.restaurant.service.BaseLoggableService;
import com.maximilian.restaurant.transaction.NamedAction;
import com.maximilian.restaurant.transaction.RepeatableSagaStep;
import com.maximilian.restaurant.transaction.RetryWithIntervalStrategy;
import com.maximilian.restaurant.transaction.SagaStep;
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
    private final OrderSagaService orderSagaService;
    private final CustomerClient customerClient;
    private final KitchenClient kitchenClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ItemRepository itemRepository, Validator validator, OrderSagaService orderSagaService, CustomerClient customerClient, KitchenClient kitchenClient) {
        super(LoggerFactory.getLogger(OrderService.class));
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.validator = validator;
        this.orderSagaService = orderSagaService;
        this.customerClient = customerClient;
        this.kitchenClient = kitchenClient;
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
            orderSagaService.performOrderCreationTransaction(getCreateOrderTransactionList(order, request));
            logger.info("Saved order " + order);
            return new OrderCreatedResponse(order.getId(), order.getOrderState().toString());
        } else {
            throw new GeneralException(getErrorMessagesTotal(violations));
        }
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

    protected List<SagaStep> getCreateOrderTransactionList(Order order, OrderRequest request) {
        List<SagaStep> result = new ArrayList<>();

        // 3 authorize card - action after which no rollbacks can be done - "final" action(money are paid), reverts cannot be done if this completed normally

        // 0 create action was already performed, just need to add compensating action(setting status of order to rejected if transaction will be rolled back)
        result.add(getCreateOrderSagaStep(order));

        // 1 check customer (and block customer for update until order is being created)
        result.add(getCheckCustomerSagaStep(order));

        // 2 create ticket in kitchen in status waiting for approval
        result.add(getCreateKitchenTickerSagaStep(order, request));

        // 4 set ticket status to approved
        result.add(getApproveTicketSagaStep(order));

        // 5 update customer status so customer can be updated
        result.add(getUpdateCustomerSagaStep(order));

        // 6 set status of order to approved
        result.add(getApproveOrderSagaStep(order));
        return result;
    }

    // step 0, compensating action
    protected SagaStep getCreateOrderSagaStep(Order order) {
        return new SagaStep() {
            @Override
            public NamedAction getAction() {
                return NamedAction.getEmptyAction("empty-create-action-" + order.getId());
            }

            @Override
            public NamedAction getCompensatingAction() {
                return new NamedAction("set-status-to-rejected-" + order.getId()) {
                    @Override
                    public void run() {
                        setOrderState(order.getId(), OrderState.REJECTED);
                    }
                };
            }
        };
    }

    // step 1, compensating action
    protected SagaStep getCheckCustomerSagaStep(Order order) {
        return new SagaStep() {
            @Override
            public NamedAction getAction() {
                return new NamedAction("add-customer-to-transaction-" + order.getId()) {
                    @Override
                    public void run() {
                        CustomerResponse customer = customerClient.getCustomerAndBlockUpdate(order.getCustomerId());
                        logger.info("Got " + customer);
                        if (!customer.getCanMakeOrders()) {
                            throw new RuntimeException("Cannot create order for this customer");
                        }
                    }
                };
            }

            @Override
            public NamedAction getCompensatingAction() {
                return new NamedAction("remove-customer-from-transaction-" + order.getId()) {
                    @Override
                    public void run() {
                        customerClient.getCustomerAndUnblockUpdate(order.getCustomerId());
                    }
                };
            }
        };
    }

    // step 2, compensating action
    protected SagaStep getCreateKitchenTickerSagaStep(Order order, OrderRequest request) {
        return new SagaStep() {
            @Override
            public NamedAction getAction() {
                return new NamedAction("create-ticket-" + order.getId()) {
                    @Override
                    public void run() {
                        KitchenOrderRequest req = new KitchenOrderRequest();
                        req.setDeliveryPoint(request.getDeliveryPoint());
                        req.setItems(request.getItems());
                        req.setOrderId(order.getId());
                        KitchenOrderResponse response = kitchenClient.createKitchenOrderWithItems(req);
                        logger.info("Created " + response);
                    }
                };
            }

            @Override
            public NamedAction getCompensatingAction() {
                return new NamedAction("reject-ticket-" + order.getId()) {
                    @Override
                    public void run() {
                        kitchenClient.rejectKitchenOrderByOuterId(order.getId());
                    }
                };
            }
        };
    }

    // step 4, repeatable action
    protected SagaStep getApproveTicketSagaStep(Order order) {
        return new RepeatableSagaStep(new RetryWithIntervalStrategy()) {
            @Override
            public NamedAction getAction() {
                return new NamedAction("approve-ticket-" + order.getId()) {
                    @Override
                    public void run() {
                        kitchenClient.approveKitchenOrderByOuterId(order.getId());
                    }
                };
            }
        };
    }

    // step 5, repeatable action
    protected SagaStep getUpdateCustomerSagaStep(Order order) {
        return new RepeatableSagaStep(new RetryWithIntervalStrategy()) {
            @Override
            public NamedAction getAction() {
                return new NamedAction("remove-customer-from-transaction-" + order.getId()) {
                    @Override
                    public void run() {
                        customerClient.getCustomerAndUnblockUpdate(order.getCustomerId());
                    }
                };
            }
        };
    }

    // step 6, repeatable action
    protected SagaStep getApproveOrderSagaStep(Order order) {
        return new RepeatableSagaStep(new RetryWithIntervalStrategy()) {
            @Override
            public NamedAction getAction() {
                return new NamedAction("approve-order-" + order.getId()) {
                    @Override
                    public void run() {
                        setOrderState(order.getId(), OrderState.APPROVED);
                    }
                };
            }
        };
    }

}
