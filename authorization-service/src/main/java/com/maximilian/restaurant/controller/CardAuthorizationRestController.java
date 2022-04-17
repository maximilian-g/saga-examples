package com.maximilian.restaurant.controller;

import com.maximilian.restaurant.data.CardDetails;
import com.maximilian.restaurant.rest.RestResponse;
import com.maximilian.restaurant.service.CardAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/card")
@Validated
public class CardAuthorizationRestController {

    private final CardAuthService cardAuthService;

    @Autowired
    public CardAuthorizationRestController(CardAuthService cardAuthService) {
        this.cardAuthService = cardAuthService;
    }

    @PostMapping
    public ResponseEntity<RestResponse> authorize(@Valid @RequestBody CardDetails details) {
        return ResponseEntity.ok(cardAuthService.authorizeCard(details));
    }

}
