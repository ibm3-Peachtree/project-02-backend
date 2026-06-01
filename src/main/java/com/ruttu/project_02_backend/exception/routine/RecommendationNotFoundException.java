package com.ruttu.project_02_backend.exception.routine;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@NoArgsConstructor
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecommendationNotFoundException extends RuntimeException{
    public RecommendationNotFoundException(String msg){
        super(msg);
    }   
}
