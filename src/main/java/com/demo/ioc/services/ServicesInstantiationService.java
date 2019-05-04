package com.demo.ioc.services;

import com.demo.ioc.exceptions.ServiceInstantiationException;
import com.demo.ioc.models.ServiceDetails;

import java.util.List;
import java.util.Set;

public interface ServicesInstantiationService {
    List<ServiceDetails> instantiateServicesAndBeans(Set<ServiceDetails> mappedServices) throws ServiceInstantiationException;
}
