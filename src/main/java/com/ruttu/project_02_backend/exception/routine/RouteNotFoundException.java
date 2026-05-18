package com.ruttu.project_02_backend.exception.routine;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RouteNotFoundException extends RuntimeException{
    public RouteNotFoundException(String msg){
        super(msg);
    }   
}
