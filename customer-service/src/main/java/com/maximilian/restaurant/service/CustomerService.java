package com.maximilian.restaurant.service;

import com.maximilian.restaurant.amqp.RabbitMQMessageProducer;
import com.maximilian.restaurant.config.CustomerMQConfig;
import com.maximilian.restaurant.entity.Customer;
import com.maximilian.restaurant.entity.CustomerStatus;
import com.maximilian.restaurant.event.OrderCreated;
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
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class CustomerService extends BaseLoggableService {

    private final CustomerRepository customerRepository;
    private final Validator validator;
    private final RabbitMQMessageProducer producer;
    private final CustomerMQConfig customerMQConfig;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, Validator validator, RabbitMQMessageProducer producer, CustomerMQConfig customerMQConfig) {
        super(LoggerFactory.getLogger(CustomerService.class));
        this.customerRepository = customerRepository;
        this.validator = validator;
        this.producer = producer;
        this.customerMQConfig = customerMQConfig;
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

    public void startTransactionForCustomerByEvent(OrderCreated event) {
        Optional<Customer> customerOptional = customerRepository.getByIdBlocking(event.getCustomerId());
        customerOptional.ifPresentOrElse(customer -> {
            if (customer.getCanMakeOrders()) {
                customer.setStatus(CustomerStatus.IN_TRANSACTION);
                customerRepository.save(customer);
                logger.info("Put customer #" + customer.getId() + " in transaction");
                // sending event further in queue which indicates that customer is valid
                producer.publish(event,
                        customerMQConfig.getInternalExchange(),
                        customerMQConfig.getInternalCustomerValidRoutingKey());
            } else {
                // rejecting order
                logger.warn("Rejecting order, customer cannot make orders.");
                producer.publish(event.getOrderId(),
                        customerMQConfig.getInternalExchange(),
                        customerMQConfig.getInternalOrderRejectRoutingKey());
            }
        }, () -> {
            logger.warn("Rejecting order, customer with id #" + event.getCustomerId() + " could not be found.");
            producer.publish(event.getOrderId(),
                    customerMQConfig.getInternalExchange(),
                    customerMQConfig.getInternalOrderRejectRoutingKey());
        });
    }

    public Customer updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer customer = getByIdBlocking(id);
        if (customer.getStatus() == CustomerStatus.IN_TRANSACTION) {
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
