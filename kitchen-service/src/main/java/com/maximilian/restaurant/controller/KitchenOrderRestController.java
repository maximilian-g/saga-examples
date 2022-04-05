package com.maximilian.restaurant.controller;

import com.maximilian.restaurant.entity.KitchenOrder;
import com.maximilian.restaurant.request.kitchen.KitchenOrderRequest;
import com.maximilian.restaurant.response.kitchen.KitchenOrderResponse;
import com.maximilian.restaurant.service.KitchenOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/kitchen/order")
@Validated
public class KitchenOrderRestController {

    private final KitchenOrderItemService kitchenOrderItemService;

    @Autowired
    public KitchenOrderRestController(KitchenOrderItemService kitchenOrderItemService) {
        this.kitchenOrderItemService = kitchenOrderItemService;
    }

    @PostMapping
    public ResponseEntity<KitchenOrderResponse> createKitchenOrderWithItems(@RequestBody @Valid KitchenOrderRequest request) {
        KitchenOrderResponse order = kitchenOrderItemService.createKitchenOrderAndConvert(request);
        UriComponentsBuilder path = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}");
        return ResponseEntity.created(path.build(order.getKitchenOrderId())).body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KitchenOrderResponse> getKitchenOrder(@PathVariable Long id) {
        return ResponseEntity.ok(kitchenOrderItemService.getKitchenOrderByIdConverted(id));
    }

    @GetMapping("/{id}/byOuterId")
    public ResponseEntity<KitchenOrderResponse> getKitchenOrderByOuterId(@PathVariable Long id) {
        return ResponseEntity.ok(kitchenOrderItemService.getKitchenOrderByOuterIdConverted(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<KitchenOrderResponse> rejectKitchenOrder(@PathVariable Long id) {
        return ResponseEntity.ok(kitchenOrderItemService.rejectKitchenOrderConverted(id));
    }

    @PutMapping("/{id}/rejectByOuter")
    public ResponseEntity<KitchenOrderResponse> rejectKitchenOrderByOuterId(@PathVariable Long id) {
        return ResponseEntity.ok(kitchenOrderItemService.rejectKitchenOrderByOuterIdConverted(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<KitchenOrderResponse> approveKitchenOrder(@PathVariable Long id) {
        return ResponseEntity.ok(kitchenOrderItemService.approveKitchenOrderConverted(id));
    }

    @PutMapping("/{id}/approveByOuter")
    public ResponseEntity<KitchenOrderResponse> approveKitchenOrderByOuterId(@PathVariable Long id) {
        return ResponseEntity.ok(kitchenOrderItemService.approveKitchenOrderByOuterIdConverted(id));
    }

}
