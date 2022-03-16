package com.cyecize.ioc.models;

import java.lang.annotation.Annotation;

public class MethodAspectHandlerDto {

    /**
     * Service details of the aspect handler.
     */
    private final ServiceDetails serviceDetails;

    /**
     * Registered annotation
     */
    private final Class<? extends Annotation> annotation;

    public MethodAspectHandlerDto(ServiceDetails serviceDetails, Class<? extends Annotation> annotation) {
        this.serviceDetails = serviceDetails;
        this.annotation = annotation;
    }

    public Class<? extends Annotation> getAnnotation() {
        return this.annotation;
    }

    public ServiceDetails getServiceDetails() {
        return this.serviceDetails;
    }
}
