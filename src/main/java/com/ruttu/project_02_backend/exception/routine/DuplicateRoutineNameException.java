package com.ruttu.project_02_backend.exception.routine;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DuplicateRoutineNameException extends RuntimeException{
    public DuplicateRoutineNameException(String msg){
        super(msg);
    }   
}
