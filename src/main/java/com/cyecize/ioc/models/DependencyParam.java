package com.cyecize.ioc.models;

import com.cyecize.ioc.annotations.Nullable;
import com.cyecize.ioc.handlers.DependencyResolver;
import com.cyecize.ioc.utils.ServiceCompatibilityUtils;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Simple POJO class that keeps information about a dependency parameter for a given service.
 */
public class DependencyParam {

    private final Class<?> dependencyType;

    private List<Class<?>> allAvailableCompatibleClasses;

    private final String instanceName;

    private final Annotation[] annotations;

    private Object instance;

    private boolean isRequired;

    /**
     * A flag about knowing if this dependency parameter could be resolved.
     * Since dependencies can be {@link Nullable}, this is required in order to make a proper check
     * if the dependency is resolved.
     */
    private boolean isValuePresent;

    private DependencyResolver dependencyResolver;

    public DependencyParam(Class<?> dependencyType, String instanceName, Annotation[] annotations) {
        this.dependencyType = dependencyType;
        this.instanceName = instanceName;
        this.annotations = annotations;
        this.setRequired(true);
        this.setValuePresent(false);
    }

    public Class<?> getDependencyType() {
        return this.dependencyType;
    }

    public List<Class<?>> getAllAvailableCompatibleClasses() {
        return this.allAvailableCompatibleClasses;
    }

    public void setAllAvailableCompatibleClasses(List<Class<?>> allAvailableCompatibleClasses) {
        this.allAvailableCompatibleClasses = allAvailableCompatibleClasses;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public Annotation[] getAnnotations() {
        return this.annotations;
    }

    public Object getInstance() {
        return this.instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public boolean isRequired() {
        return this.isRequired;
    }

    public void setRequired(boolean required) {
        this.isRequired = required;
    }

    public boolean isValuePresent() {
        return this.isValuePresent;
    }

    public void setValuePresent(boolean valuePresent) {
        this.isValuePresent = valuePresent;
    }

    public DependencyResolver getDependencyResolver() {
        return this.dependencyResolver;
    }

    public void setDependencyResolver(DependencyResolver dependencyResolver) {
        this.dependencyResolver = dependencyResolver;
    }

    public boolean isUnresolved() {
        return this.getInstance() == null && this.isValuePresent();
    }

    public boolean isCompatible(ServiceDetails serviceDetails) {
        return ServiceCompatibilityUtils.isServiceCompatible(serviceDetails, this.dependencyType, this.instanceName);
    }
}
