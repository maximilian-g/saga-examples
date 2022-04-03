package com.maximilian.restaurant.service;

import com.maximilian.restaurant.entity.Customer;
import com.maximilian.restaurant.entity.CustomerStatus;
import com.maximilian.restaurant.repository.CustomerRepository;
import com.maximilian.restaurant.response.CustomerRequest;
import com.maximilian.restaurant.rest.exception.GeneralException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Service
@Transactional
public class CustomerService extends BaseLoggableService  {

    private final CustomerRepository customerRepository;
    private final Validator validator;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, Validator validator) {
        super(LoggerFactory.getLogger(CustomerService.class));
        this.customerRepository = customerRepository;
        this.validator = validator;
        initCustomer();
    }

    private void initCustomer() {
        Customer customer = new Customer();
        customer.setStatus(CustomerStatus.READY_FOR_UPDATE);
        customer.setCanMakeOrders(true);
        customer.setName("Vasiliy");
        customer = customerRepository.save(customer);
        logger.info("Created " + customer);
    }

    public Customer createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setCanMakeOrders(true);
        customer.setStatus(CustomerStatus.READY_FOR_UPDATE);
        Set<ConstraintViolation<Customer>> violations = getViolations(customer, validator);
        if (violations.isEmpty()) {
            customer = customerRepository.saveAndFlush(customer);
            logger.info("Saved customer " + customer);
            return customer;
        } else {
            throw new GeneralException(getErrorMessagesTotal(violations));
        }
    }

    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Customer with id #" + id + " not found", HttpStatus.NOT_FOUND));
    }

    public Customer changeStatus(Long id, CustomerStatus status) {
        Customer customer = customerRepository.getByIdBlocking(id)
                .orElseThrow(() -> new GeneralException("Customer with id #" + id + " not found", HttpStatus.NOT_FOUND));
        customer.setStatus(status);
        customer = customerRepository.save(customer);
        return customer;
    }

}
