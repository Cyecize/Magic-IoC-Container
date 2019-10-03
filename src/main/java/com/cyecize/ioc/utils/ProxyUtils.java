package com.cyecize.ioc.utils;

import com.cyecize.ioc.models.InvocationHandler;
import com.cyecize.ioc.models.ServiceDetails;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ProxyUtils {

    public static void createProxyInstance(ServiceDetails serviceDetails, Object[] constructorParams) {
        final ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(serviceDetails.getServiceType());
        final Class<?> cls = proxyFactory.createClass();

        Object proxyInstance;
        try {

            proxyInstance = Arrays.stream(cls.getDeclaredConstructors())
                    .filter(ctr -> {
                        if (ctr.getParameterCount() != constructorParams.length) {
                            return false;
                        }

                        final Class<?>[] parameterTypes = ctr.getParameterTypes();

                        for (int i = 0; i < parameterTypes.length; i++) {
                            if (constructorParams[i] != null && !parameterTypes[i].isAssignableFrom(constructorParams[i].getClass())) {
                                return false;
                            }
                        }

                        ctr.setAccessible(true);
                        return true;
                    }).findFirst().get().newInstance(constructorParams);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        ((ProxyObject) proxyInstance).setHandler(new InvocationHandler(serviceDetails));

        serviceDetails.setProxyInstance(proxyInstance);
    }
}
