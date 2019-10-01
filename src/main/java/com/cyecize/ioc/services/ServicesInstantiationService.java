package com.cyecize.ioc.services;

import com.cyecize.ioc.models.ServiceDetails;
import com.cyecize.ioc.exceptions.ServiceInstantiationException;

import java.util.List;
import java.util.Set;

public interface ServicesInstantiationService {
    List<ServiceDetails> instantiateServicesAndBeans(Set<ServiceDetails> mappedServices) throws ServiceInstantiationException;
}
