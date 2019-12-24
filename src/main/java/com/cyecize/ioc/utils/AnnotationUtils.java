package com.cyecize.ioc.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class AnnotationUtils {

    public static Object getAnnotationValue(Annotation annotation) {
        try {
            final Method method = annotation.annotationType().getMethod("value");
            return method.invoke(annotation);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
