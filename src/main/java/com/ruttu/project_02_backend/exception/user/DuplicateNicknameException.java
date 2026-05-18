package com.ruttu.project_02_backend.exception.user;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DuplicateNicknameException extends RuntimeException{
    public DuplicateNicknameException(String msg){
        super(msg);
    }   
}
