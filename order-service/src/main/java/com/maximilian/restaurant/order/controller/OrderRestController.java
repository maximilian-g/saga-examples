package com.maximilian.restaurant.order.controller;

import com.maximilian.restaurant.order.service.OrderService;
import com.maximilian.restaurant.request.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/order")
@Validated
public class OrderRestController {

    private final Logger logger = LoggerFactory.getLogger(OrderRestController.class);
    private final OrderService orderService;

    @Autowired
    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOrder(@PathVariable Long id) {
        logger.info("Getting order with id " + id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest request) {
        orderService.createOrder(request);
        return ResponseEntity.ok().build();
    }

}
