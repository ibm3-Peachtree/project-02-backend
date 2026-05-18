package com.ruttu.project_02_backend.exception.feed;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidImageFormatException extends RuntimeException{
    public InvalidImageFormatException(String msg){
        super(msg);
    }   
}
