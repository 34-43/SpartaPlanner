package com.sparta.spartaplanner.exception;

public class PasswordFailException extends FailedRequestException {
    public PasswordFailException() {
        super("Password doesn't match");
    }
}
