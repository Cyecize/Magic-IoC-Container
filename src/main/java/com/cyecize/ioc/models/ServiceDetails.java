package com.cyecize.ioc.models;

import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.enums.ScopeType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
     * The name of the instance or null if no name has been given.
     */
    private String instanceName;

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
     * Holds information for service's scope.
     */
    private ScopeType scopeType;

    /**
     * The reference to all @Bean (or a custom one) annotated methods.
     */
    private Collection<ServiceBeanDetails> beans;

    /**
     * Array of fields within the service that are annotated with @{@link Autowired}
     */
    private Field[] autowireAnnotatedFields;

    /**
     * Collection with details about resolved dependencies from the target constructor.
     */
    private LinkedList<DependencyParam> resolvedConstructorParams;

    /**
     * Collection with details about resolved {@link Autowired} field dependencies.
     */
    private LinkedList<DependencyParam> resolvedFields;

    private final Map<Method, List<MethodAspectHandlerDto>> methodAspectHandlers = new HashMap<>();

    protected ServiceDetails() {

    }

    public ServiceDetails(Class<?> serviceType,
                          Annotation annotation, Constructor<?> targetConstructor,
                          String instanceName,
                          Method postConstructMethod, Method preDestroyMethod,
                          ScopeType scopeType,
                          Field[] autowireAnnotatedFields) {
        this();
        this.setServiceType(serviceType);
        this.setAnnotation(annotation);
        this.setTargetConstructor(targetConstructor);
        this.setInstanceName(instanceName);
        this.setPostConstructMethod(postConstructMethod);
        this.setPreDestroyMethod(preDestroyMethod);
        this.setScopeType(scopeType);
        this.setAutowireAnnotatedFields(autowireAnnotatedFields);
    }

    public Class<?> getServiceType() {
        return this.serviceType;
    }

    void setServiceType(Class<?> serviceType) {
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

    public String getInstanceName() {
        return this.instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Object getActualInstance() {
        return this.instance;
    }

    public Object getInstance() {
        if (this.proxyInstance != null) {
            return this.proxyInstance;
        }

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

    public boolean hasProxyInstance() {
        return this.proxyInstance != null;
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

    public ScopeType getScopeType() {
        return this.scopeType;
    }

    public void setScopeType(ScopeType scopeType) {
        this.scopeType = scopeType;
    }

    public Collection<ServiceBeanDetails> getBeans() {
        return this.beans;
    }

    public void setBeans(Collection<ServiceBeanDetails> beans) {
        this.beans = beans;
    }

    public Field[] getAutowireAnnotatedFields() {
        return this.autowireAnnotatedFields;
    }

    public void setAutowireAnnotatedFields(Field[] autowireAnnotatedFields) {
        this.autowireAnnotatedFields = autowireAnnotatedFields;
    }

    public LinkedList<DependencyParam> getResolvedConstructorParams() {
        return this.resolvedConstructorParams;
    }

    public void setResolvedConstructorParams(LinkedList<DependencyParam> resolvedConstructorParams) {
        this.resolvedConstructorParams = resolvedConstructorParams;
    }

    public LinkedList<DependencyParam> getResolvedFields() {
        return this.resolvedFields;
    }

    public void setResolvedFields(LinkedList<DependencyParam> resolvedFields) {
        this.resolvedFields = resolvedFields;
    }

    public Map<Method, List<MethodAspectHandlerDto>> getMethodAspectHandlers() {
        return this.methodAspectHandlers;
    }

    public void setMethodAspectHandlers(Map<Method, List<MethodAspectHandlerDto>> methodAspectHandlers) {
        this.methodAspectHandlers.putAll(methodAspectHandlers);
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
