package com.maximilian.restaurant.order.service;


import com.maximilian.restaurant.transaction.SagaService;
import com.maximilian.restaurant.transaction.SagaStep;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderSagaService extends SagaService {

    protected OrderSagaService() {
        super(LoggerFactory.getLogger(OrderSagaService.class));
    }

    @Async
    public void performOrderCreationTransaction(List<SagaStep> stepsOfTransaction) {
        performActions(stepsOfTransaction);
    }

}
