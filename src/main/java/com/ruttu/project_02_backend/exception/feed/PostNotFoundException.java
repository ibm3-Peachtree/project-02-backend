package com.ruttu.project_02_backend.exception.feed;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PostNotFoundException extends RuntimeException{
    public PostNotFoundException(String msg){
        super(msg);
    }   
}
