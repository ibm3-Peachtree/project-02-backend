package com.ruttu.project_02_backend.exception.report;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WeeklyReportNotFoundException extends RuntimeException{
    public WeeklyReportNotFoundException(String msg){
        super(msg);
    }   
}
