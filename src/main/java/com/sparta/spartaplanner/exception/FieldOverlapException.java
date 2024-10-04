package com.sparta.spartaplanner.exception;

public class FieldOverlapException extends FailedRequestException {
    public FieldOverlapException(String fieldName) {
        super(fieldName + " is overlapping in database");
    }
}
