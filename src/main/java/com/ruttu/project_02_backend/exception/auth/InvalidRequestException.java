package com.ruttu.project_02_backend.exception.auth;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidRequestException extends RuntimeException{
    public InvalidRequestException(String msg){
        super(msg);
    }
}
