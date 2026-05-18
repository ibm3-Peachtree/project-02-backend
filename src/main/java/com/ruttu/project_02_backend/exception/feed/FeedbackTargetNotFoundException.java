package com.ruttu.project_02_backend.exception.feed;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FeedbackTargetNotFoundException extends RuntimeException{
    public FeedbackTargetNotFoundException(String msg){
        super(msg);
    }   
}
