package com.maximilian.restaurant.controller;

import com.maximilian.restaurant.service.KitchenOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/kitchen/item")
@Validated
public class KitchenItemRestController {

    private final KitchenOrderItemService kitchenOrderItemService;

    @Autowired
    public KitchenItemRestController(KitchenOrderItemService kitchenOrderItemService) {
        this.kitchenOrderItemService = kitchenOrderItemService;
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<?> getKitchenOrder(@PathVariable Long id) {
//
//    }

}
