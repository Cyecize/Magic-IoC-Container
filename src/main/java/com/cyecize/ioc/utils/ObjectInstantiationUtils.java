package com.cyecize.ioc.utils;

import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.exceptions.BeanInstantiationException;
import com.cyecize.ioc.exceptions.PostConstructException;
import com.cyecize.ioc.exceptions.PreDestroyExecutionException;
import com.cyecize.ioc.exceptions.ServiceInstantiationException;
import com.cyecize.ioc.models.DependencyParam;
import com.cyecize.ioc.models.ServiceBeanDetails;
import com.cyecize.ioc.models.ServiceDetails;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ObjectInstantiationUtils {
    private static final String INVALID_PARAMETERS_COUNT_MSG = "Invalid parameters count for '%s'.";

    public static void createInstance(ServiceDetails serviceDetails) {
        serviceDetails.setInstance(createNewInstance(serviceDetails));
    }

    public static void createInstance(ServiceDetails serviceDetails,
                                      Object[] constructorParams,
                                      Object[] autowiredFieldInstances) {
        serviceDetails.setInstance(createNewInstance(serviceDetails, constructorParams, autowiredFieldInstances));
    }

    public static Object createNewInstance(ServiceDetails serviceDetails) {
        final Object[] constructorParams = serviceDetails.getResolvedConstructorParams().stream()
                .map(DependencyParam::getInstance)
                .toArray(Object[]::new);

        final Object[] fieldParams = serviceDetails.getResolvedFields().stream()
                .map(DependencyParam::getInstance)
                .toArray(Object[]::new);

        return createNewInstance(serviceDetails, constructorParams, fieldParams);
    }

    /**
     * Creates an instance for a service.
     * Invokes the PostConstruct method.
     *
     * @param serviceDetails    the given service details.
     * @param constructorParams instantiated dependencies.
     */
    public static Object createNewInstance(ServiceDetails serviceDetails,
                                           Object[] constructorParams,
                                           Object[] autowiredFieldInstances) throws ServiceInstantiationException {
        final Constructor<?> targetConstructor = serviceDetails.getTargetConstructor();

        if (constructorParams.length != targetConstructor.getParameterCount()) {
            throw new ServiceInstantiationException(String.format(
                    INVALID_PARAMETERS_COUNT_MSG,
                    serviceDetails.getServiceType().getName()
            ));
        }

        try {
            final Object instance = targetConstructor.newInstance(constructorParams);
            serviceDetails.setInstance(instance);
            setAutowiredFieldInstances(serviceDetails, autowiredFieldInstances, instance);
            invokePostConstruct(serviceDetails, instance);

            return instance;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ServiceInstantiationException(e.getMessage(), e);
        }
    }

    /**
     * Iterates all {@link Autowired} annotated fields and sets them a given instance.
     *
     * @param serviceDetails          - given service details.
     * @param autowiredFieldInstances - field instances.
     */
    private static void setAutowiredFieldInstances(ServiceDetails serviceDetails,
                                                   Object[] autowiredFieldInstances,
                                                   Object instance) throws IllegalAccessException {
        final Field[] autowireAnnotatedFields = serviceDetails.getAutowireAnnotatedFields();

        for (int i = 0; i < autowireAnnotatedFields.length; i++) {
            autowireAnnotatedFields[i].set(instance, autowiredFieldInstances[i]);
        }
    }

    /**
     * Invokes post construct method if one is present for a given service.
     *
     * @param serviceDetails - the given service.
     */
    private static void invokePostConstruct(ServiceDetails serviceDetails,
                                            Object instance) throws PostConstructException {
        if (serviceDetails.getPostConstructMethod() == null) {
            return;
        }

        try {
            serviceDetails.getPostConstructMethod().invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new PostConstructException(e.getMessage(), e);
        }
    }


    /**
     * Creates an instance for a bean by invoking its origin method
     * and passing the instance of the service in which the bean has been declared.
     *
     * @param serviceBeanDetails the given bean details.
     */
    public static void createBeanInstance(ServiceBeanDetails serviceBeanDetails) throws BeanInstantiationException {
        serviceBeanDetails.setInstance(createNewInstance(serviceBeanDetails));
    }

    public static Object createNewInstance(ServiceBeanDetails serviceBeanDetails) {
        final Method originMethod = serviceBeanDetails.getOriginMethod();
        final Object rootInstance = serviceBeanDetails.getRootService().getActualInstance();

        try {
            return originMethod.invoke(rootInstance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanInstantiationException(e.getMessage(), e);
        }
    }

    /**
     * Sets the instance to null.
     * Invokes post construct method for the given service details if one is present.
     *
     * @param serviceDetails given service details.
     */
    public static void destroyInstance(ServiceDetails serviceDetails) throws PreDestroyExecutionException {
        if (serviceDetails.getPreDestroyMethod() != null) {
            try {
                serviceDetails.getPreDestroyMethod().invoke(serviceDetails.getActualInstance());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new PreDestroyExecutionException(e.getMessage(), e);
            }
        }

        serviceDetails.setInstance(null);
    }
}
