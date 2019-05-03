package com.demo.ioc.exceptions;

public class ServiceInstantiationException extends RuntimeException {

    public ServiceInstantiationException(String message) {
        super(message);
    }

    public ServiceInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
