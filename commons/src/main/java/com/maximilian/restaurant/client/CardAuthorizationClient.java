package com.maximilian.restaurant.client;

import com.maximilian.restaurant.data.CardDetails;
import com.maximilian.restaurant.rest.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "authorization-service", url = "${service.authorization.url}", path = "/api/v1/card")
public interface CardAuthorizationClient {

    @PostMapping
    RestResponse authorize(@RequestBody CardDetails details);

}
