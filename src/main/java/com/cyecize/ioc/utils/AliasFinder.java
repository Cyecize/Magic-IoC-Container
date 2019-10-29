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

    public static boolean isAliasAnnotationPresent(Annotation[] annotations, Class<? extends Annotation> requiredAnnotation) {

        for (Annotation declaredAnnotation : annotations) {
            final Class<?> alias = getAliasAnnotation(declaredAnnotation, requiredAnnotation);

            if (alias != null) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAnnotationPresent(Annotation[] annotations, Class<? extends Annotation> requiredAnnotation) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == requiredAnnotation) {
                return true;
            }
        }

        return isAliasAnnotationPresent(annotations, requiredAnnotation);
    }
}
