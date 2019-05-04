package com.demo.ioc.models;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceDetails {

    private Class<?> serviceType;

    private Annotation annotation;

    private Constructor<?> targetConstructor;

    private Object instance;

    private Method postConstructMethod;

    private Method preDestroyMethod;

    private Method[] beans;

    private final List<ServiceDetails> dependantServices;

    public ServiceDetails() {
        this.dependantServices = new ArrayList<>();
    }

    public ServiceDetails(Class<?> serviceType,
                          Annotation annotation, Constructor<?> targetConstructor,
                          Method postConstructMethod, Method preDestroyMethod,
                          Method[] beans) {
        this();
        this.setServiceType(serviceType);
        this.setAnnotation(annotation);
        this.setTargetConstructor(targetConstructor);
        this.setPostConstructMethod(postConstructMethod);
        this.setPreDestroyMethod(preDestroyMethod);
        this.setBeans(beans);
    }

    public Class<?> getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(Class<?> serviceType) {
        this.serviceType = serviceType;
    }

    public Annotation getAnnotation() {
        return this.annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Constructor<?> getTargetConstructor() {
        return this.targetConstructor;
    }

    public void setTargetConstructor(Constructor<?> targetConstructor) {
        this.targetConstructor = targetConstructor;
    }

    public Object getInstance() {
        return this.instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getPostConstructMethod() {
        return this.postConstructMethod;
    }

    public void setPostConstructMethod(Method postConstructMethod) {
        this.postConstructMethod = postConstructMethod;
    }

    public Method getPreDestroyMethod() {
        return this.preDestroyMethod;
    }

    public void setPreDestroyMethod(Method preDestroyMethod) {
        this.preDestroyMethod = preDestroyMethod;
    }

    public Method[] getBeans() {
        return this.beans;
    }

    public void setBeans(Method[] beans) {
        this.beans = beans;
    }

    public List<ServiceDetails> getDependantServices() {
        return Collections.unmodifiableList(this.dependantServices);
    }

    public void addDependantService(ServiceDetails serviceDetails) {
        this.dependantServices.add(serviceDetails);
    }

    @Override
    public int hashCode() {
        if (this.serviceType == null) {
            return super.hashCode();
        }

        return this.serviceType.hashCode();
    }

    @Override
    public String toString() {
        if (this.serviceType == null) {
            return super.toString();
        }

        return this.serviceType.getName();
    }
}
