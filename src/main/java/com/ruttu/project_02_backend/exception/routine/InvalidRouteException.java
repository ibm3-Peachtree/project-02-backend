package com.ruttu.project_02_backend.exception.routine;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@NoArgsConstructor
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRouteException extends RuntimeException{
    public InvalidRouteException(String msg){
        super(msg);
    }   
}
