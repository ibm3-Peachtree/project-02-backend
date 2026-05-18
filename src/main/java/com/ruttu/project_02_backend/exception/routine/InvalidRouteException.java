package com.ruttu.project_02_backend.exception.routine;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidRouteException extends RuntimeException{
    public InvalidRouteException(String msg){
        super(msg);
    }   
}
