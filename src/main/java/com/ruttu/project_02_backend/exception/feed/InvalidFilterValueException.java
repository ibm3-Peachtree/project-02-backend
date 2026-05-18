package com.ruttu.project_02_backend.exception.feed;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidFilterValueException extends RuntimeException{
    public InvalidFilterValueException(String msg){
        super(msg);
    }   
}
