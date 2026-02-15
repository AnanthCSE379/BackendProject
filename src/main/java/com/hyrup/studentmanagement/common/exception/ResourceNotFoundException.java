package com.hyrup.studentmanagement.common.exception;

//custom exception for resource not found, error 404
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
