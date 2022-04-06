package com.maximilian.restaurant.client;

import com.maximilian.restaurant.request.kitchen.KitchenTicketRequest;
import com.maximilian.restaurant.response.kitchen.KitchenTicketResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(name = "kitchen-service", url = "${service.kitchen.url}", path = "/api/v1/kitchen/ticket")
public interface KitchenClient {

    @PostMapping
    KitchenTicketResponse createKitchenTicketWithItems(@RequestBody @Valid KitchenTicketRequest request);

    @GetMapping("/{id}")
    KitchenTicketResponse getKitchenTicket(@PathVariable(name = "id") Long id);

    @PutMapping("/{id}/reject")
    KitchenTicketResponse rejectKitchenTicket(@PathVariable(name = "id") Long id);

    @PutMapping("/{id}/approve")
    KitchenTicketResponse approveKitchenTicket(@PathVariable(name = "id") Long id);

}
