package com.demo.ioc.exceptions;

public class ClassLocationException extends RuntimeException {

    public ClassLocationException(String message) {
        super(message);
    }

    public ClassLocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
