package com.alexduzi.dscommerce.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends CustomError {

    private List<FieldMessage> errors = new ArrayList<>();

    public ValidationError() {
    }

    public ValidationError(Instant timestamp, Integer status, String error, String path) {
        super(timestamp, status, error, path);
    }

    public void addError(String fieldName, String message) {
        errors.removeIf(x -> x.getFieldName().equalsIgnoreCase(fieldName));
        errors.add(new FieldMessage(message, fieldName));
    }

    public List<FieldMessage> getErrors() {
        return errors;
    }
}
