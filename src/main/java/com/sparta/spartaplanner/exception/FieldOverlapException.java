package com.sparta.spartaplanner.exception;

// 새로운 Row 를 생성할 때, UNIQUE 필드가 중복될 경우 발생하는 예외

public class FieldOverlapException extends FailedRequestException {
    public FieldOverlapException(String fieldName) {
        super(fieldName + " is overlapping in database");
    }
}
