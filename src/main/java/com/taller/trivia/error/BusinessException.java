package com.taller.trivia.error;

public class BusinessException extends RuntimeException{
    public BusinessException(String message) {
        super(message);
    }
}