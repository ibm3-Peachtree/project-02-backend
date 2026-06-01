package com.ruttu.project_02_backend.exception.routine;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@NoArgsConstructor
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateRoutineNameException extends RuntimeException{
    public DuplicateRoutineNameException(String msg){
        super(msg);
    }   
}
