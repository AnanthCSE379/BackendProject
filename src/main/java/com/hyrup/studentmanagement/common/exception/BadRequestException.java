package com.hyrup.studentmanagement.common.exception;
 
 //custom exception for bad requests to backend
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
