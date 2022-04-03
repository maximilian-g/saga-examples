package com.maximilian.restaurant.rest;

import com.maximilian.restaurant.entity.CustomerStatus;
import com.maximilian.restaurant.response.CustomerRequest;
import com.maximilian.restaurant.response.CustomerResponse;
import com.maximilian.restaurant.service.CustomerService;
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
@RequestMapping(path = "/api/v1/customer")
@Validated
public class CustomerRestController {

    private final CustomerService customerService;

    @Autowired
    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PutMapping("/{id}/start_transaction")
    public ResponseEntity<CustomerResponse> getCustomerAndBlockUpdate(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.changeStatus(id, CustomerStatus.IN_TRANSACTION).toResponse());
    }

    @PutMapping("/{id}/end_transaction")
    public ResponseEntity<CustomerResponse> getCustomerAndUnblockUpdate(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.changeStatus(id, CustomerStatus.READY_FOR_UPDATE).toResponse());
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody @Valid CustomerRequest request) {
        CustomerResponse customerResponse = customerService.createCustomer(request).toResponse();
        UriComponentsBuilder path = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}");
        return ResponseEntity.created(path.build(customerResponse.getId())).body(customerResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id).toResponse());
    }

}
