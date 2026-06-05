package com.ruttu.project_02_backend.exception.feed;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FeedGlobalExceptionHandler {
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ProblemDetail> fileUploadHandler(FileUploadException fileUploadException){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("파일 업로드 실패");
        problemDetail.setDetail(fileUploadException.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
