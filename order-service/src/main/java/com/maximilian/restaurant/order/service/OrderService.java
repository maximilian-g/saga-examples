package com.maximilian.restaurant.order.service;

import com.maximilian.restaurant.client.CardAuthorizationClient;
import com.maximilian.restaurant.client.CustomerClient;
import com.maximilian.restaurant.client.KitchenClient;
import com.maximilian.restaurant.data.CardDetails;
import com.maximilian.restaurant.order.entity.Order;
import com.maximilian.restaurant.order.entity.OrderState;
import com.maximilian.restaurant.order.repository.OrderRepository;
import com.maximilian.restaurant.request.kitchen.KitchenTicketRequest;
import com.maximilian.restaurant.request.order.OrderRequest;
import com.maximilian.restaurant.response.customer.CustomerResponse;
import com.maximilian.restaurant.response.kitchen.KitchenTicketResponse;
import com.maximilian.restaurant.response.order.OrderCreatedResponse;
import com.maximilian.restaurant.response.order.OrderResponse;
import com.maximilian.restaurant.rest.RestResponse;
import com.maximilian.restaurant.rest.exception.GeneralException;
import com.maximilian.restaurant.service.BaseLoggableService;
import com.maximilian.restaurant.transaction.NamedAction;
import com.maximilian.restaurant.transaction.RepeatableSagaStep;
import com.maximilian.restaurant.transaction.RetryWithIntervalStrategy;
import com.maximilian.restaurant.transaction.SagaContext;
import com.maximilian.restaurant.transaction.SagaStep;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService extends BaseLoggableService {

    private final OrderRepository orderRepository;
    private final Validator validator;
    private final OrderSagaService orderSagaService;

    private final CustomerClient customerClient;
    private final KitchenClient kitchenClient;
    private final CardAuthorizationClient cardAuthorizationClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, Validator validator, OrderSagaService orderSagaService, CustomerClient customerClient, KitchenClient kitchenClient, CardAuthorizationClient cardAuthorizationClient) {
        super(LoggerFactory.getLogger(OrderService.class));
        this.orderRepository = orderRepository;
        this.validator = validator;
        this.orderSagaService = orderSagaService;
        this.customerClient = customerClient;
        this.kitchenClient = kitchenClient;
        this.cardAuthorizationClient = cardAuthorizationClient;
    }

    public OrderCreatedResponse createOrder(OrderRequest request) {
        logger.info("Got request: " + request);

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setOrderState(OrderState.WAITING_FOR_APPROVAL);

        validate(order, validator);

        order = orderRepository.saveAndFlush(order);
        orderSagaService.performOrderCreationTransaction(getCreateOrderTransactionList(order, request));
        logger.info("Saved order " + order);

        return new OrderCreatedResponse(order.getId(), order.getOrderState().toString());
    }

    protected void setOrderState(Long id, OrderState state) {
        Order orderToUpdate = getOrderById(id);
        orderToUpdate.setOrderState(state);

        validate(orderToUpdate, validator);

        orderRepository.save(orderToUpdate);
    }

    public OrderResponse getOrderResponseById(Long id) {
        Order order = getOrderById(id);
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setStatus(order.getOrderState().toString());

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

    protected List<SagaStep> getCreateOrderTransactionList(Order order, OrderRequest request) {
        List<SagaStep> result = new ArrayList<>();


        // 0 create action was already performed, just need to add compensating action(setting status of order to rejected if transaction will be rolled back)
        result.add(getCreateOrderSagaStep(order));

        // 1 check customer (and block customer for update until order is being created)
        result.add(getCheckCustomerSagaStep(order));

        // 2 create ticket in kitchen in status waiting for approval
        result.add(getCreateKitchenTickerSagaStep(order, request));

        // 3 authorize card - action after which no rollbacks can be done - "final" action(money are paid), reverts cannot be done if this completed normally
        result.add(getCardAuthSagaStep(order, request.getCardDetails()));

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
                        KitchenTicketRequest req = new KitchenTicketRequest();
                        req.setDeliveryPoint(request.getDeliveryPoint());
                        req.setItems(request.getItems());
                        req.setOrderId(order.getId());
                        KitchenTicketResponse response = kitchenClient.createKitchenTicketWithItems(req);
                        logger.info("Created " + response);
                    }
                };
            }

            @Override
            public NamedAction getCompensatingAction() {
                return new NamedAction("reject-ticket-" + order.getId()) {
                    @Override
                    public void run() {
                        kitchenClient.rejectKitchenTicket(order.getId());
                    }
                };
            }
        };
    }

    // step 3, final action
    protected SagaStep getCardAuthSagaStep(Order order, CardDetails details) {
        return new SagaStep() {

            @Override
            public void afterExecute(SagaContext context, NamedAction action) {
                context.setReachedFinalAction(true);
                context.getLogger().info("Reached final transaction by action " + action.getName());
            }

            @Override
            public NamedAction getAction() {
                return new NamedAction("authorize-card-for" + order.getId()) {
                    @Override
                    public void run() {
                        RestResponse response = cardAuthorizationClient.authorize(details);
                        logger.info("Got response " + response);
                        if (!response.isSuccess()) {
                            throw new GeneralException(response.getMessage());
                        }
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
                        kitchenClient.approveKitchenTicket(order.getId());
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
