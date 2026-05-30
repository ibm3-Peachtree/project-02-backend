package com.ruttu.project_02_backend.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AddressGlobalExceptionHandler {
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ProblemDetail> addressNotFoundHandler(AddressNotFoundException addressNotFoundException){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("주소 없음");
        problemDetail.setDetail(addressNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }
    @ExceptionHandler(LoginRequiredException.class)
    public ResponseEntity<ProblemDetail> loginRequiredHandler(LoginRequiredException loginRequiredException){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("로그인 필요");
        problemDetail.setDetail(loginRequiredException.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }
}
