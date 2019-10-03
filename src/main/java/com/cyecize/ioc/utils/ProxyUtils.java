package com.cyecize.ioc.utils;

import com.cyecize.ioc.models.InvocationHandlerImpl;
import com.cyecize.ioc.models.MethodInvocationHandlerImpl;
import com.cyecize.ioc.models.ServiceBeanDetails;
import com.cyecize.ioc.models.ServiceDetails;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
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

        ((ProxyObject) proxyInstance).setHandler(new MethodInvocationHandlerImpl(serviceDetails));

        serviceDetails.setProxyInstance(proxyInstance);
    }

    public static void createBeanProxyInstance(ServiceBeanDetails serviceDetails) {
        if (!serviceDetails.getServiceType().isInterface()) {
            return;
        }

       final Object proxyInstance = Proxy.newProxyInstance(
                serviceDetails.getServiceType().getClassLoader(),
                new Class[]{serviceDetails.getServiceType()},
                new InvocationHandlerImpl(serviceDetails));

        serviceDetails.setProxyInstance(proxyInstance);
    }
}
