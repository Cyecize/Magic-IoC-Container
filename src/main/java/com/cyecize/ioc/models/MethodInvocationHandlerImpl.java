package com.cyecize.ioc.models;

import com.cyecize.ioc.handlers.MethodInvocationChain;
import com.cyecize.ioc.handlers.ServiceMethodAspectHandler;
import javassist.util.proxy.MethodHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MethodInvocationHandlerImpl implements MethodHandler {

    private final ServiceDetails serviceDetails;

    public MethodInvocationHandlerImpl(ServiceDetails serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        try {
            if (!this.serviceDetails.getMethodAspectHandlers().containsKey(thisMethod)) {
                return thisMethod.invoke(this.serviceDetails.getActualInstance(), args);
            }

            final List<MethodAspectHandlerDto> aspectHandlers = this.serviceDetails.getMethodAspectHandlers().get(thisMethod);

            final AtomicReference<MethodInvocationChain> invocationChain = new AtomicReference<>(() -> thisMethod.invoke(
                    this.serviceDetails.getActualInstance(), args
            ));

            for (MethodAspectHandlerDto serviceAspectHandler : aspectHandlers) {
                final ServiceMethodAspectHandler<Annotation> aspectHandler = (ServiceMethodAspectHandler<Annotation>)
                        serviceAspectHandler.getServiceDetails().getInstance();

                final MethodInvocationChain next = invocationChain.get();
                invocationChain.set(() -> aspectHandler.proceed(
                        thisMethod.getAnnotation(serviceAspectHandler.getAnnotation()),
                        thisMethod,
                        args,
                        next
                ));
            }

            return invocationChain.get().proceed();
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }
}
