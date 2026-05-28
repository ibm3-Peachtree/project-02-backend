package com.ruttu.project_02_backend.exception.auth;

public class MissingTokenException extends RuntimeException {
    public MissingTokenException(String message) {
        super(message);
    }
}
