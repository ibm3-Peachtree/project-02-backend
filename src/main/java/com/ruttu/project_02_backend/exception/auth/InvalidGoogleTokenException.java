package com.ruttu.project_02_backend.exception.auth;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidGoogleTokenException extends RuntimeException{
    public InvalidGoogleTokenException(String msg){
        super(msg);
    }   
}
