package com.maximilian.restaurant.controller;

import com.maximilian.restaurant.entity.KitchenItem;
import com.maximilian.restaurant.request.kitchen.KitchenItemRequest;
import com.maximilian.restaurant.response.kitchen.KitchenItemResponse;
import com.maximilian.restaurant.service.KitchenTicketItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/v1/kitchen/item")
@Validated
public class KitchenItemRestController {

    private final KitchenTicketItemService kitchenTicketItemService;

    @Autowired
    public KitchenItemRestController(KitchenTicketItemService kitchenTicketItemService) {
        this.kitchenTicketItemService = kitchenTicketItemService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<KitchenItemResponse> getKitchenOrder(@RequestBody @Valid KitchenItemRequest request) {
        KitchenItem item = kitchenTicketItemService.createItem(request);
        UriComponentsBuilder path = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}");
        return ResponseEntity.created(path.build(item.getId())).body(item.toResponse());
    }

    @PutMapping("/{id}")
    public ResponseEntity<KitchenItemResponse> getKitchenOrder(@PathVariable Long id,
                                                               @RequestBody @Valid KitchenItemRequest request) {
        return ResponseEntity.ok(kitchenTicketItemService.updateItem(id, request).toResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KitchenItemResponse> getKitchenOrder(@PathVariable Long id) {
        return ResponseEntity.ok(kitchenTicketItemService.getItemById(id).toResponse());
    }

}
