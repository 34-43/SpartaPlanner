package com.sparta.spartaplanner.exception;

// 제시된 기본 키인 id 로 Row 를 특정할 수 없을 때 발생하는 예외

public class IdNotFoundException extends FailedRequestException {
    public IdNotFoundException(String entityName, Long id) {
        super(entityName + " with id '" + id + "' not found");
    }
}
