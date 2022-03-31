package com.maximilian.restaurant.service;

import org.slf4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class BaseLoggableService {

    protected final Logger logger;

    protected BaseLoggableService(Logger logger) {
        this.logger = logger;
    }

    protected <T> Set<ConstraintViolation<T>> getViolations(T entity, Validator validator) {
        return validator.validate(entity);
    }

    protected <T> String getErrorMessagesTotal(Set<ConstraintViolation<T>> violations) {
        List<String> errorMessages = new ArrayList<>(violations.size());
        for (ConstraintViolation<T> violation : violations) {
            errorMessages.add(violation.getMessage());
        }
        return String.join(", ", errorMessages) + ".";
    }

}
