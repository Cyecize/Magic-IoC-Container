package com.cyecize.ioc.models;

public class ResolvedServiceDto {
    //Producer service is always a service
    private final ServiceDetails producerService;

    //Might be a Bean
    private final ServiceDetails actualService;

    public ResolvedServiceDto(ServiceDetails producerService, ServiceDetails actualService) {
        this.producerService = producerService;
        this.actualService = actualService;
    }

    public ServiceDetails getProducerService() {
        return this.producerService;
    }

    public ServiceDetails getActualService() {
        return this.actualService;
    }
}