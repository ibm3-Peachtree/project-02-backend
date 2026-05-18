package com.ruttu.project_02_backend.exception.notification;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidSettingValueException extends RuntimeException{
    public InvalidSettingValueException(String msg){
        super(msg);
    }   
}
