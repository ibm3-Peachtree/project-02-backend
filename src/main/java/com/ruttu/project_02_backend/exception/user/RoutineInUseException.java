package com.ruttu.project_02_backend.exception.user;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@NoArgsConstructor
@ResponseStatus(HttpStatus.CONFLICT)
public class RoutineInUseException extends RuntimeException{
    public RoutineInUseException(String msg){
        super(msg);
    }   
}
