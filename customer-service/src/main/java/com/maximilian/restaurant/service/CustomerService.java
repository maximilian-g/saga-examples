package com.maximilian.restaurant.service;

import com.maximilian.restaurant.entity.Customer;
import com.maximilian.restaurant.entity.CustomerStatus;
import com.maximilian.restaurant.repository.CustomerRepository;
import com.maximilian.restaurant.request.customer.CustomerRequest;
import com.maximilian.restaurant.request.customer.UpdateCustomerRequest;
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
        return saveIfValid(customer);
    }

    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new GeneralException("Customer with id #" + id + " not found", HttpStatus.NOT_FOUND));
    }

    public Customer changeStatus(Long id, CustomerStatus status) {
        Customer customer = getByIdBlocking(id);
        customer.setStatus(status);
        customer = customerRepository.save(customer);
        return customer;
    }

    public Customer updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer customer = getByIdBlocking(id);
        if(customer.getStatus() == CustomerStatus.IN_TRANSACTION) {
            throw new GeneralException("Customer #" + id + " temporary cannot be updated");
        }
        customer.setCanMakeOrders(request.getCanMakeOrders());
        return saveIfValid(customer);
    }

    protected Customer getByIdBlocking(Long id) {
        return customerRepository.getByIdBlocking(id)
                .orElseThrow(() -> new GeneralException("Customer with id #" + id + " not found", HttpStatus.NOT_FOUND));
    }

    protected Customer saveIfValid(Customer customer) {
        Set<ConstraintViolation<Customer>> violations = getViolations(customer, validator);
        if (violations.isEmpty()) {
            customer = customerRepository.save(customer);
            logger.info("Saved customer " + customer);
            return customer;
        } else {
            throw new GeneralException(getErrorMessagesTotal(violations));
        }
    }

}
