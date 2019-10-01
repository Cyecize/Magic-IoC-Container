package com.cyecize.ioc.exceptions;

public class PreDestroyExecutionException extends RuntimeException {
    public PreDestroyExecutionException(String message) {
        super(message);
    }

    public PreDestroyExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
