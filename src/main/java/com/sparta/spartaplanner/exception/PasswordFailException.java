package com.sparta.spartaplanner.exception;

// 암호가 일치하지 않아 업데이트를 진행할 수 없을 때 발생하는 예외

public class PasswordFailException extends FailedRequestException {
    public PasswordFailException() {
        super("Password doesn't match");
    }
}
