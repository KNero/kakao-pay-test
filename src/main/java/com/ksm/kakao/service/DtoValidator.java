package com.ksm.kakao.service;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

@Component
public class DtoValidator {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public <T> void validate(T object) throws ValidationException {
        Set<ConstraintViolation<T>> result = validator.validate(object);

        Iterator<ConstraintViolation<T>> iterator = result.iterator();
        if (iterator.hasNext()) {
            ConstraintViolation<T> o = iterator.next();
            String message = "field: " + o.getPropertyPath().toString() + ", message: " + o.getMessage();
            throw new ValidationException(message);
        }
    }
}
