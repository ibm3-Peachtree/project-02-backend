package com.ruttu.project_02_backend.exception.auth;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ExpiredJwtTokenException extends RuntimeException{
    public ExpiredJwtTokenException(String msg){
        super(msg);
    }   
}
