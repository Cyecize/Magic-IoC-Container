package com.demo.ioc.services;

import com.demo.ioc.exceptions.AlreadyInitializedException;
import com.demo.ioc.models.ServiceDetails;

import java.lang.annotation.Annotation;
import java.util.List;

public interface DependencyContainer {

    void init(List<ServiceDetails> servicesAndBeans, ObjectInstantiationService instantiationService) throws AlreadyInitializedException;

    void reload(ServiceDetails serviceDetails, boolean reloadDependantServices);

    <T> T reload(T service);

    <T> T reload(T service, boolean reloadDependantServices);

    <T> T getService(Class<T> serviceType);

    ServiceDetails getServiceDetails(Class<?> serviceType);

    List<ServiceDetails> getServicesByAnnotation(Class<? extends Annotation> annotationType);

    List<Object> getAllServices();

    List<ServiceDetails> getAllServiceDetails();
}
