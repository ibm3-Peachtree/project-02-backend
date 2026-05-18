package com.ruttu.project_02_backend.exception.feed;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidSortTypeException extends RuntimeException{
    public InvalidSortTypeException(String msg){
        super(msg);
    }   
}
