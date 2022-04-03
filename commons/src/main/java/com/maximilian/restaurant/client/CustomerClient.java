package com.maximilian.restaurant.client;

import com.maximilian.restaurant.response.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "customer-service", url = "${service.customer.url}", path = "/api/v1/customer")
public interface CustomerClient {

    @PutMapping("/{id}/start_transaction")
    CustomerResponse getCustomerAndBlockUpdate(@PathVariable(name = "id") Long id);

    @PutMapping("/{id}/end_transaction")
    CustomerResponse getCustomerAndUnblockUpdate(@PathVariable(name = "id") Long id);

    @GetMapping("/{id}")
    CustomerResponse getCustomer(@PathVariable(name = "id") Long id);

}
