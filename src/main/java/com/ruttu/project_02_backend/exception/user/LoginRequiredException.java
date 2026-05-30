package com.ruttu.project_02_backend.exception.user;

public class LoginRequiredException extends RuntimeException {
    public LoginRequiredException(String message) {
        super(message);
    }
}
