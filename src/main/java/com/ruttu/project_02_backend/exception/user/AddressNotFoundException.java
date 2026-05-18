package com.ruttu.project_02_backend.exception.user;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AddressNotFoundException extends RuntimeException{
    public AddressNotFoundException(String msg){
        super(msg);
    }   
}
