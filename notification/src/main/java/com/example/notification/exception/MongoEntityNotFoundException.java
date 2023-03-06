package com.example.notification.exception;

public class MongoEntityNotFoundException extends RuntimeException {

    public MongoEntityNotFoundException(String message) {
        super(message);
    }
}
