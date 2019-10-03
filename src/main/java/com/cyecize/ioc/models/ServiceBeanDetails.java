package com.cyecize.ioc.models;

import java.lang.reflect.Method;

/**
 * Descendant of @ServiceDetails that is made to contain additional bean details.
 * <p>
 * In this case some of the fields in the ServiceDetails will be left null.
 */
public class ServiceBeanDetails extends ServiceDetails {

    /**
     * Reference to the method that returns instance of this type of bean.
     */
    private final Method originMethod;

    /**
     * The service from this bean was created.
     */
    private final ServiceDetails rootService;

    public ServiceBeanDetails(Class<?> beanType, Method originMethod, ServiceDetails rootService) {
        this.setServiceType(beanType);
        this.setBeans(new Method[0]);
        this.originMethod = originMethod;
        this.rootService = rootService;
    }

    public Method getOriginMethod() {
        return this.originMethod;
    }

    public ServiceDetails getRootService() {
        return this.rootService;
    }

    @Override
    public Object getProxyInstance() {
        if (super.getProxyInstance() != null) {
            return super.getProxyInstance();
        }

        return this.getActualInstance();
    }

    public boolean hasProxyInstance() {
        return super.getProxyInstance() != null;
    }
}
