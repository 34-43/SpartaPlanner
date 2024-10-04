package com.sparta.spartaplanner.exception;

// 요청에 포함된 값으로 쿼리를 수행할 수 없거나 결과에 문제가 있을 때 발생하는 예외

public class FailedRequestException extends RuntimeException {
    public FailedRequestException(String message) {
        super("Request Failed : " + message);
    }
}
