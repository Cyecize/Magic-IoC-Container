package com.cyecize.ioc.models;

import com.cyecize.ioc.handlers.DependencyResolver;
import com.cyecize.ioc.utils.DependencyParamUtils;

import java.lang.annotation.Annotation;

/**
 * Simple POJO class that keeps information about a dependency parameter for a given service.
 */
public class DependencyParam {

    private final Class<?> dependencyType;

    private final String instanceName;

    private final Annotation[] annotations;

    private boolean isRequired;

    private DependencyResolver dependencyResolver;

    //Service details of the dependency provider.
    private ServiceDetails serviceDetails;

    private Object instance;

    public DependencyParam(Class<?> dependencyType, String instanceName, Annotation[] annotations) {
        this.dependencyType = dependencyType;
        this.instanceName = instanceName;
        this.annotations = annotations;
        this.setRequired(true);
    }

    public Class<?> getDependencyType() {
        return this.dependencyType;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public Annotation[] getAnnotations() {
        return this.annotations;
    }

    public boolean isRequired() {
        return this.isRequired;
    }

    public void setRequired(boolean required) {
        this.isRequired = required;
    }

    public DependencyResolver getDependencyResolver() {
        return this.dependencyResolver;
    }

    public void setDependencyResolver(DependencyResolver dependencyResolver) {
        this.dependencyResolver = dependencyResolver;
    }

    public void setServiceDetails(ServiceDetails serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Object getInstance() {
        final Object instance;
        if (this.dependencyResolver != null) {
            instance = this.instance;
        } else if (this.serviceDetails != null) {
            instance = this.serviceDetails.getInstance();
        } else {
            instance = null;
        }

        if (instance == null && this.isRequired) {
            throw new IllegalStateException(String.format(
                    "Trying to get instance for dependency '%s' but there is none",
                    this.dependencyType
            ));
        }

        return instance;
    }

    public boolean isCompatible(ServiceDetails serviceDetails) {
        return DependencyParamUtils.isServiceCompatible(serviceDetails, this.dependencyType, this.instanceName);
    }
}
