package com.cyecize.ioc.models;

import javassist.util.proxy.MethodHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodInvocationHandlerImpl implements MethodHandler {

    private final ServiceDetails serviceDetails;

    public MethodInvocationHandlerImpl(ServiceDetails serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        try {
            return thisMethod.invoke(this.serviceDetails.getActualInstance(), args);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }
}
