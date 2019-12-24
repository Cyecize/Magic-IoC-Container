package com.cyecize.ioc.models;

import com.cyecize.ioc.enums.ScopeType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

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
     * The service from which this bean was created.
     */
    private final ServiceDetails rootService;

    public ServiceBeanDetails(Class<?> beanType, Method originMethod,
                              ServiceDetails rootService, Annotation annotation,
                              ScopeType scopeType,
                              String instanceName) {
        super.setServiceType(beanType);
        super.setBeans(new ArrayList<>(0));
        this.originMethod = originMethod;
        this.rootService = rootService;
        super.setAnnotation(annotation);
        super.setScopeType(scopeType);
        super.setInstanceName(instanceName);
    }

    public Method getOriginMethod() {
        return this.originMethod;
    }

    public ServiceDetails getRootService() {
        return this.rootService;
    }
}
