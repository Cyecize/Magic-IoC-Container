package com.cyecize.ioc.utils;

import com.cyecize.ioc.annotations.AliasFor;

import java.lang.annotation.Annotation;

public class AliasFinder {
    public static Class<? extends Annotation> getAliasAnnotation(Annotation declaredAnnotation, Class<? extends Annotation> requiredAnnotation) {
        if (declaredAnnotation.annotationType().isAnnotationPresent(AliasFor.class)) {
            final Class<? extends Annotation> aliasValue = declaredAnnotation.annotationType().getAnnotation(AliasFor.class).value();
            if (aliasValue == requiredAnnotation) {
                return aliasValue;
            }
        }

        return null;
    }

    public static Annotation getAnnotation(Annotation[] annotations, Class<? extends Annotation> requiredAnnotation) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == requiredAnnotation || getAliasAnnotation(annotation, requiredAnnotation) != null) {
                return annotation;
            }
        }

        return null;
    }

    public static boolean isAnnotationPresent(Annotation[] annotations, Class<? extends Annotation> requiredAnnotation) {
        return getAnnotation(annotations, requiredAnnotation) != null;
    }
}
