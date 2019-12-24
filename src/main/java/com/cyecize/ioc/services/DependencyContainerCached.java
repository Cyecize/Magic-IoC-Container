package com.cyecize.ioc.services;

import com.cyecize.ioc.models.ServiceDetails;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DependencyContainerCached extends DependencyContainerInternal {

    private final Map<Class<?>, ServiceDetails> cachedServices;

    private final Map<Class<?>, Collection<ServiceDetails>> cachedImplementations;

    private final Map<Class<? extends Annotation>, Collection<ServiceDetails>> cachedServicesByAnnotation;

    public DependencyContainerCached() {
        this.cachedServices = new HashMap<>();
        this.cachedImplementations = new HashMap<>();
        this.cachedServicesByAnnotation = new HashMap<>();
    }

    @Override
    public ServiceDetails getServiceDetails(Class<?> serviceType) {
        if (this.cachedServices.containsKey(serviceType)) {
            return this.cachedServices.get(serviceType);
        }

        final ServiceDetails serviceDetails = super.getServiceDetails(serviceType);

        if (serviceDetails != null) {
            this.cachedServices.put(serviceType, serviceDetails);
        }

        return serviceDetails;
    }

    @Override
    public Collection<ServiceDetails> getImplementations(Class<?> serviceType) {
        if (this.cachedImplementations.containsKey(serviceType)) {
            return this.cachedImplementations.get(serviceType);
        }

        final Collection<ServiceDetails> implementations = super.getImplementations(serviceType);

        this.cachedImplementations.put(serviceType, implementations);

        return implementations;
    }

    @Override
    public Collection<ServiceDetails> getServicesByAnnotation(Class<? extends Annotation> annotationType) {
        if (this.cachedServicesByAnnotation.containsKey(annotationType)) {
            return this.cachedServicesByAnnotation.get(annotationType);
        }

        final Collection<ServiceDetails> servicesByAnnotation = super.getServicesByAnnotation(annotationType);

        this.cachedServicesByAnnotation.put(annotationType, servicesByAnnotation);

        return servicesByAnnotation;
    }
}
