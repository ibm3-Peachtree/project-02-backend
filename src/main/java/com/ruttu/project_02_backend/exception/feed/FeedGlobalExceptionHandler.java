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
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ProblemDetail> postNotFoundHandler(PostNotFoundException postNotFoundException){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("게시글 없음");
        problemDetail.setDetail(postNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> accessDeniedHandler(AccessDeniedException accessDeniedException){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("권한 없음");
        problemDetail.setDetail(accessDeniedException.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }
    @ExceptionHandler(InvalidImageFormatException.class)
    public ResponseEntity<ProblemDetail> invalidImageHandler(InvalidImageFormatException invalidImageFormatException){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("잘못된 형식");
        problemDetail.setDetail(invalidImageFormatException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
}
