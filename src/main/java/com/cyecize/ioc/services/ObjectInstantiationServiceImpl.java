package com.cyecize.ioc.services;

import com.cyecize.ioc.exceptions.BeanInstantiationException;
import com.cyecize.ioc.exceptions.PostConstructException;
import com.cyecize.ioc.exceptions.ServiceInstantiationException;
import com.cyecize.ioc.exceptions.PreDestroyExecutionException;
import com.cyecize.ioc.models.ServiceBeanDetails;
import com.cyecize.ioc.models.ServiceDetails;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@link ObjectInstantiationService} implementation.
 */
public class ObjectInstantiationServiceImpl implements ObjectInstantiationService {
    private static final String INVALID_PARAMETERS_COUNT_MSG = "Invalid parameters count for '%s'.";

    /**
     * Creates an instance for a service.
     * Invokes the PostConstruct method.
     *
     * @param serviceDetails    the given service details.
     * @param constructorParams instantiated dependencies.
     */
    @Override
    public void createInstance(ServiceDetails serviceDetails, Object... constructorParams) throws ServiceInstantiationException {
        final Constructor targetConstructor = serviceDetails.getTargetConstructor();

        if (constructorParams.length != targetConstructor.getParameterCount()) {
            throw new ServiceInstantiationException(String.format(INVALID_PARAMETERS_COUNT_MSG, serviceDetails.getServiceType().getName()));
        }

        try {
            final Object instance = targetConstructor.newInstance(constructorParams);
            serviceDetails.setInstance(instance);
            this.invokePostConstruct(serviceDetails);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ServiceInstantiationException(e.getMessage(), e);
        }
    }

    /**
     * Invokes post construct method if one is present for a given service.
     *
     * @param serviceDetails - the given service.
     */
    private void invokePostConstruct(ServiceDetails serviceDetails) throws PostConstructException {
        if (serviceDetails.getPostConstructMethod() == null) {
            return;
        }

        try {
            serviceDetails.getPostConstructMethod().invoke(serviceDetails.getActualInstance());
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
    @Override
    public void createBeanInstance(ServiceBeanDetails serviceBeanDetails) throws BeanInstantiationException {
        final Method originMethod = serviceBeanDetails.getOriginMethod();
        final Object rootInstance = serviceBeanDetails.getRootService().getActualInstance();

        try {
            final Object instance = originMethod.invoke(rootInstance);
            serviceBeanDetails.setInstance(instance);
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
    @Override
    public void destroyInstance(ServiceDetails serviceDetails) throws PreDestroyExecutionException {
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
