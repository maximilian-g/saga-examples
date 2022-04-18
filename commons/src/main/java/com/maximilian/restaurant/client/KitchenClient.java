package com.maximilian.restaurant.client;

import com.maximilian.restaurant.response.kitchen.KitchenTicketResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "kitchen-service", url = "${service.kitchen.url}", path = "/api/v1/kitchen/ticket")
public interface KitchenClient {

    @GetMapping("/{id}")
    KitchenTicketResponse getKitchenTicket(@PathVariable(name = "id") Long id);

}
