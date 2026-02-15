package com.hyrup.studentmanagement.common.exception;

//exception for error 401
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
