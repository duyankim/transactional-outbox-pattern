package com.github.account.common;

import jakarta.validation.*;

import java.util.Set;

public abstract class SelfValidating<T> {

    private final Validator validator;

    public SelfValidating() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    /**
     * 모든 Bean Validation 제약 조건을 검증
     */
    protected void validateSelf() {
        Set<ConstraintViolation<T>> violations = validator.validate((T) this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
