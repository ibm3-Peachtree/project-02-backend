package com.ruttu.project_02_backend.exception.routine;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DuplicateRoutineTargetArrivalTimeException extends RuntimeException{
    public DuplicateRoutineTargetArrivalTimeException(String msg){
        super(msg);
    }   
}
