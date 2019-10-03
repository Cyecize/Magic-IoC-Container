package com.cyecize.ioc.models;

import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;

public class InvocationHandler implements MethodHandler {

    private final ServiceDetails serviceDetails;

    public InvocationHandler(ServiceDetails serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        return thisMethod.invoke(this.serviceDetails.getActualInstance(), args);
    }
}
