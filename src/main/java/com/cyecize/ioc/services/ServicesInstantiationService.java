package com.cyecize.ioc.services;

import com.cyecize.ioc.exceptions.ServiceInstantiationException;
import com.cyecize.ioc.models.ServiceDetails;

import java.util.Collection;
import java.util.Set;

public interface ServicesInstantiationService {
    Collection<ServiceDetails> instantiateServicesAndBeans(Set<ServiceDetails> mappedServices) throws ServiceInstantiationException;
}
