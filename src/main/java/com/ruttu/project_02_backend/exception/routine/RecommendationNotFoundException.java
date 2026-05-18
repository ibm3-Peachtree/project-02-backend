package com.ruttu.project_02_backend.exception.routine;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RecommendationNotFoundException extends RuntimeException{
    public RecommendationNotFoundException(String msg){
        super(msg);
    }   
}
