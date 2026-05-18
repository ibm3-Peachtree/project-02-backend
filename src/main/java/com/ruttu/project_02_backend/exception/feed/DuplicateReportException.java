package com.ruttu.project_02_backend.exception.feed;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DuplicateReportException extends RuntimeException{
    public DuplicateReportException(String msg){
        super(msg);
    }   
}
