package com.cyecize.ioc.handlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * This service can be used to register an annotation that will be used on other services as an aspect.
 * When will be annotated with the annotation registered here, a proxy will be created for that service and the code
 * described in the implementation of the handler will be executed before (or after) the actual method execution.
 *
 * @param <T> - annotation to be used on service methods.
 */
public interface ServiceMethodAspectHandler<T extends Annotation> {
    Object proceed(T annotation,
                   Method method,
                   Object[] params,
                   MethodInvocationChain invocationChain) throws Exception;
}
