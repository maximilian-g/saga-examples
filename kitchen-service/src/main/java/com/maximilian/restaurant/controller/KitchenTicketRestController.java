package com.maximilian.restaurant.controller;

import com.maximilian.restaurant.request.kitchen.KitchenTicketRequest;
import com.maximilian.restaurant.response.kitchen.KitchenTicketResponse;
import com.maximilian.restaurant.service.KitchenTicketItemService;
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
@RequestMapping("/api/v1/kitchen/ticket")
@Validated
public class KitchenTicketRestController {

    private final KitchenTicketItemService kitchenTicketItemService;

    @Autowired
    public KitchenTicketRestController(KitchenTicketItemService kitchenTicketItemService) {
        this.kitchenTicketItemService = kitchenTicketItemService;
    }

    @PostMapping
    public ResponseEntity<KitchenTicketResponse> createKitchenOrderWithItems(@RequestBody @Valid KitchenTicketRequest request) {
        KitchenTicketResponse order = kitchenTicketItemService.createKitchenTicketAndConvert(request);
        UriComponentsBuilder path = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}");
        return ResponseEntity.created(path.build(order.getTicketId())).body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KitchenTicketResponse> getKitchenOrder(@PathVariable Long id) {
        return ResponseEntity.ok(kitchenTicketItemService.getKitchenTicketByIdConverted(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<KitchenTicketResponse> rejectKitchenOrder(@PathVariable Long id) {
        return ResponseEntity.ok(kitchenTicketItemService.rejectKitchenTicketConverted(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<KitchenTicketResponse> approveKitchenOrder(@PathVariable Long id) {
        return ResponseEntity.ok(kitchenTicketItemService.approveKitchenTicketConverted(id));
    }

}
