package com.ruttu.project_02_backend.exception.user;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidAddressFormatException extends RuntimeException{
    public InvalidAddressFormatException(String msg){
        super(msg);
    }   
}
