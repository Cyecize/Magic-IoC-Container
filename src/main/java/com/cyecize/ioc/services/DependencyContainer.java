package com.cyecize.ioc.services;

import com.cyecize.ioc.exceptions.AlreadyInitializedException;
import com.cyecize.ioc.models.ServiceDetails;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

public interface DependencyContainer {

    void init(Collection<Class<?>> locatedClasses, List<ServiceDetails> servicesAndBeans, ObjectInstantiationService instantiationService) throws AlreadyInitializedException;

    void reload(ServiceDetails serviceDetails, boolean reloadDependantServices);

    <T> T reload(T service);

    <T> T reload(T service, boolean reloadDependantServices);

    <T> T getService(Class<T> serviceType);

    ServiceDetails getServiceDetails(Class<?> serviceType);

    List<ServiceDetails> getServicesByAnnotation(Class<? extends Annotation> annotationType);

    List<Object> getAllServices();

    List<ServiceDetails> getAllServiceDetails();

    Collection<Class<?>> getLocatedClasses();
}
