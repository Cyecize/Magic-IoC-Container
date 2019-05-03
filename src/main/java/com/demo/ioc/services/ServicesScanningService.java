package com.demo.ioc.services;

import com.demo.ioc.models.ServiceDetails;

import java.util.Set;

public interface ServicesScanningService {

    Set<ServiceDetails<?>> mapServices(Set<Class<?>> locatedClasses);
}
