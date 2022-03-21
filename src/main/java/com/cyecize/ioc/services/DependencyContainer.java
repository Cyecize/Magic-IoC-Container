package com.cyecize.ioc.services;

import com.cyecize.ioc.models.ServiceDetails;

import java.lang.annotation.Annotation;
import java.util.Collection;

public interface DependencyContainer {

    void reload(ServiceDetails serviceDetails);

    void reload(Class<?> serviceType);

    void update(Object service);

    void update(Class<?> serviceType, Object serviceInstance);

    void update(Class<?> serviceType, Object serviceInstance, boolean destroyOldInstance);

    <T> T getService(Class<T> serviceType);

    <T> T getService(Class<?> serviceType, String instanceName);

    <T> T getNewInstance(Class<?> serviceType);

    <T> T getNewInstance(Class<?> serviceType, String instanceName);

    ServiceDetails getServiceDetails(Class<?> serviceType);

    ServiceDetails getServiceDetails(Class<?> serviceType, String instanceName);

    Collection<Class<?>> getAllScannedClasses();

    Collection<ServiceDetails> getImplementations(Class<?> serviceType);

    Collection<ServiceDetails> getServicesByAnnotation(Class<? extends Annotation> annotationType);

    Collection<ServiceDetails> getAllServices();
}
