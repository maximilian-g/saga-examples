package com.maximilian.restaurant.client;

import com.maximilian.restaurant.request.kitchen.KitchenOrderRequest;
import com.maximilian.restaurant.response.kitchen.KitchenOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(name = "kitchen-service", url = "${service.kitchen.url}", path = "/api/v1/kitchen/order")
public interface KitchenClient {

    @PostMapping
    KitchenOrderResponse createKitchenOrderWithItems(@RequestBody @Valid KitchenOrderRequest request);

    @GetMapping("/{id}")
    KitchenOrderResponse getKitchenOrder(@PathVariable(name = "id") Long id);

    @GetMapping("/{id}/byOuterId")
    KitchenOrderResponse getKitchenOrderByOuterId(@PathVariable(name = "id") Long id);

    @PutMapping("/{id}/reject")
    ResponseEntity<KitchenOrderResponse> rejectKitchenOrder(@PathVariable(name = "id") Long id);

    @PutMapping("/{id}/rejectByOuter")
    ResponseEntity<KitchenOrderResponse> rejectKitchenOrderByOuterId(@PathVariable(name = "id") Long id);

    @PutMapping("/{id}/approve")
    ResponseEntity<KitchenOrderResponse> approveKitchenOrder(@PathVariable(name = "id") Long id);

    @PutMapping("/{id}/approveByOuter")
    ResponseEntity<KitchenOrderResponse> approveKitchenOrderByOuterId(@PathVariable(name = "id") Long id);

}
