package com.devops.accommodation.exception;

public class AppException extends RuntimeException {

    private final String message;

    public AppException(String message) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
