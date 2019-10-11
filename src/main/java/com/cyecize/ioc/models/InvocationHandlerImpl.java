package com.cyecize.ioc.models;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InvocationHandlerImpl implements InvocationHandler {

    private final ServiceDetails serviceDetails;

    public InvocationHandlerImpl(ServiceDetails serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(this.serviceDetails.getActualInstance(), args);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }
}
