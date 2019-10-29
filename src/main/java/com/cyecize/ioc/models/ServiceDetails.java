package com.cyecize.ioc.models;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Simple POJO class that holds information about a given class.
 * <p>
 * This is needed since that way we have the data scanned only once and we
 * will improve performance at runtime since the data in only collected once
 * at startup.
 */
public class ServiceDetails {

    private static final String PROXY_ALREADY_CREATED_MSG = "Proxy instance already created.";

    /**
     * The type of the service.
     */
    private Class<?> serviceType;

    /**
     * The annotation used to map the service (@Service or a custom one).
     */
    private Annotation annotation;

    /**
     * The constructor that will be used to create an instance of the service.
     */
    private Constructor<?> targetConstructor;

    /**
     * Service instance.
     */
    private Object instance;

    /**
     * Proxy instance that will be injected into services instead of actual instance.
     */
    private Object proxyInstance;

    /**
     * Reference to the post construct method if any.
     */
    private Method postConstructMethod;

    /**
     * Reference to the pre destroy method if any.
     */
    private Method preDestroyMethod;

    /**
     * The reference to all @Bean (or a custom one) annotated methods.
     */
    private Method[] beans;

    private Field[] autowireAnnotatedFields;

    /**
     * List of all services that depend on this one.
     */
    private final List<ServiceDetails> dependantServices;

    public ServiceDetails() {
        this.dependantServices = new ArrayList<>();
    }

    public ServiceDetails(Class<?> serviceType,
                          Annotation annotation, Constructor<?> targetConstructor,
                          Method postConstructMethod, Method preDestroyMethod,
                          Method[] beans,
                          Field[] autowireAnnotatedFields) {
        this();
        this.setServiceType(serviceType);
        this.setAnnotation(annotation);
        this.setTargetConstructor(targetConstructor);
        this.setPostConstructMethod(postConstructMethod);
        this.setPreDestroyMethod(preDestroyMethod);
        this.setBeans(beans);
        this.setAutowireAnnotatedFields(autowireAnnotatedFields);
    }

    public Class<?> getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(Class<?> serviceType) {
        this.serviceType = serviceType;
    }

    public Annotation getAnnotation() {
        return annotation;
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

    public Object getActualInstance() {
        return this.instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Object getProxyInstance() {
        return this.proxyInstance;
    }

    public void setProxyInstance(Object proxyInstance) {
        if (this.proxyInstance != null) {
            throw new IllegalArgumentException(PROXY_ALREADY_CREATED_MSG);
        }

        this.proxyInstance = proxyInstance;
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

    public Field[] getAutowireAnnotatedFields() {
        return this.autowireAnnotatedFields;
    }

    public void setAutowireAnnotatedFields(Field[] autowireAnnotatedFields) {
        this.autowireAnnotatedFields = autowireAnnotatedFields;
    }

    public List<ServiceDetails> getDependantServices() {
        return Collections.unmodifiableList(this.dependantServices);
    }

    public void addDependantService(ServiceDetails serviceDetails) {
        this.dependantServices.add(serviceDetails);
    }

    /**
     * We are using the serviceType hashcode in order to make this class unique
     * when using in in sets.
     *
     * @return hashcode.
     */
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
