package com.example.AuthService.exception.domain;

public class ObjectNotFoundException extends RuntimeException{

    private static final long serialVersioniUID = 1L;

    public ObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectNotFoundException(String message) {
        super(message);
    }
}
