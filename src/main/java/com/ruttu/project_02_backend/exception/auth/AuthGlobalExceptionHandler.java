package com.ruttu.project_02_backend.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthGlobalExceptionHandler{
    @ExceptionHandler(InvalidGoogleTokenException.class)
    public ResponseEntity<ProblemDetail> usersDuplicateHandler(InvalidGoogleTokenException invalidGoogleTokenException){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("유효하지 않은 토큰");
        problemDetail.setDetail(invalidGoogleTokenException.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }
}
