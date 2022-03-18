package com.cyecize.ioc.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
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


    public static Class<?> getRawType(ParameterizedType type) {
        return getRawType(type, null);
    }

    /**
     * Search and find real generic value.
     * Eg:
     * private List<String> - will return String
     * private List<Set<List<Integer>>> - will return Integer
     *
     * @param type - parameterized type
     * @return raw type
     */
    private static Class<?> getRawType(ParameterizedType type, Class<?> lastType) {
        final Type actualTypeArgument = type.getActualTypeArguments()[0];
        if (actualTypeArgument instanceof Class) {
            return (Class<?>) actualTypeArgument;
        }

        if (actualTypeArgument instanceof WildcardType) {
            return lastType;
        }

        return getRawType(
                (ParameterizedType) actualTypeArgument,
                (Class<?>) ((ParameterizedType) actualTypeArgument).getRawType()
        );
    }
}
