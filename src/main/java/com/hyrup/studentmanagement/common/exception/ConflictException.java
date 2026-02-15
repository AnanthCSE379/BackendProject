package com.hyrup.studentmanagement.common.exception;

//custom exception for error 409
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
