package com.ruttu.project_02_backend.exception.auth;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidJwtTokenException extends RuntimeException{
    public InvalidJwtTokenException(String msg){
        super(msg);
    }   
}
