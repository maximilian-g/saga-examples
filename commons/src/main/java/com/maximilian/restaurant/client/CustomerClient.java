package com.maximilian.restaurant.client;

import com.maximilian.restaurant.request.customer.CustomerRequest;
import com.maximilian.restaurant.request.customer.UpdateCustomerRequest;
import com.maximilian.restaurant.response.customer.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "customer-service", url = "${service.customer.url}", path = "/api/v1/customer")
public interface CustomerClient {

    @PutMapping("/{id}")
    CustomerResponse changeOrderCreationPossibility(@PathVariable(name = "id") Long id,
                                                    @RequestBody UpdateCustomerRequest request);

    @PostMapping
    CustomerResponse createCustomer(@RequestBody CustomerRequest request);

    @GetMapping("/{id}")
    CustomerResponse getCustomer(@PathVariable(name = "id") Long id);

}
