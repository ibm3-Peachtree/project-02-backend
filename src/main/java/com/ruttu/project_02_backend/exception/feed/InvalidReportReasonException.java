package com.ruttu.project_02_backend.exception.feed;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidReportReasonException extends RuntimeException{
    public InvalidReportReasonException(String msg){
        super(msg);
    }   
}
