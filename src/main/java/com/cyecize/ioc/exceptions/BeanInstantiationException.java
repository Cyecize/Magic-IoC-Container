package com.cyecize.ioc.exceptions;

public class BeanInstantiationException extends ServiceInstantiationException {
    public BeanInstantiationException(String message) {
        super(message);
    }

    public BeanInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
