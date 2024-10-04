package com.sparta.spartaplanner.exception;

public class FailedRequestException extends RuntimeException {
    public FailedRequestException(String message) {
        super("Request Failed : " + message);
    }
}
