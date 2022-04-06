package com.maximilian.restaurant;

import com.maximilian.restaurant.data.CardDetails;
import com.maximilian.restaurant.rest.RestResponse;
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

    @PostMapping
    public ResponseEntity<RestResponse> authorize(@Valid @RequestBody CardDetails details) {
        // mock of card authorization
        try {
            Thread.sleep((long)(5000 + (10000 * Math.random())));
        } catch (Exception ignored) {

        }
        RestResponse restResponse = new RestResponse();
        restResponse.setSuccess(Double.compare(Math.random(), 0.5) > 0);
        if(restResponse.isSuccess()) {
            restResponse.setMessage("Authorized card ending with *" + details.getCardNumber().substring(details.getCardNumber().length() - 4));
        } else {
            restResponse.setMessage("Failed to authorize card ending with *" + details.getCardNumber().substring(details.getCardNumber().length() - 4));
        }
        return ResponseEntity.ok(restResponse);
    }

}
