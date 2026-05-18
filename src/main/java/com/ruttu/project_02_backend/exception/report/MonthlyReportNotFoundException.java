package com.ruttu.project_02_backend.exception.report;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MonthlyReportNotFoundException extends RuntimeException{
    public MonthlyReportNotFoundException(String msg){
        super(msg);
    }   
}
