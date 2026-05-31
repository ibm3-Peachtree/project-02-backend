package com.ruttu.project_02_backend.exception.auth;

public class UnauthorizedException extends RuntimeException {
  public UnauthorizedException(String message) {
    super(message);
  }
}
