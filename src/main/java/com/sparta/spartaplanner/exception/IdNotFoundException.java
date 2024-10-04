package com.sparta.spartaplanner.exception;

public class IdNotFoundException extends FailedRequestException {
    public IdNotFoundException(String entityName, Long id) {
        super(entityName + " with id '" + id + "' not found");
    }
}
