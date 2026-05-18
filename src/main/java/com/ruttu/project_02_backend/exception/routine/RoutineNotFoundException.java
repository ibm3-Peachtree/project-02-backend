package com.ruttu.project_02_backend.exception.routine;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RoutineNotFoundException extends RuntimeException{
    public RoutineNotFoundException(String msg){
        super(msg);
    }   
}
