package com.cyecize.ioc.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

public class GenericsUtils {

    /**
     * Gets the generic type arguments of a given class.
     * EG.
     * class Abc implements Handler<String> {}
     * If we call getGenericTypeArguments(Abc.class, Handler.class) we will get String as a result.
     *
     * @param cls          - class to be looked up.
     * @param genericClass - generic class or interface from which we need to extract the types.
     * @return -
     */
    public static Type[] getGenericTypeArguments(Class<?> cls, Class<?> genericClass) {
        final Optional<ParameterizedType> genericClsType = Arrays.stream(cls.getGenericInterfaces())
                .filter(type -> type instanceof ParameterizedType)
                .filter(type -> ((ParameterizedType) type).getRawType() == genericClass)
                .map(type -> (ParameterizedType) type)
                .findFirst();

        if (genericClsType.isPresent()) {
            return genericClsType.get().getActualTypeArguments();
        }

        if (cls.getGenericSuperclass() != Object.class) {
            return getGenericTypeArguments((Class<?>) cls.getGenericSuperclass(), genericClass);
        }

        return null;
    }
}
