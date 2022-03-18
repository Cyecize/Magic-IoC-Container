package com.cyecize.ioc.utils;

import com.cyecize.ioc.exceptions.ServiceInstantiationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class CollectionUtils {
    public static <T> Collection<T> createInstanceOfCollection(Class<?> collectionCls) {
        if (collectionCls.isAssignableFrom(ArrayList.class)) {
            return new ArrayList<>();
        }

        if (collectionCls.isAssignableFrom(HashSet.class)) {
            return new HashSet<>();
        }

        throw new ServiceInstantiationException("Cannot autowire collection of type %s.");
    }
}
