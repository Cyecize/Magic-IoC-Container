package com.cyecize.ioc.services;

import com.cyecize.ioc.exceptions.ServiceInstantiationException;
import com.cyecize.ioc.models.EnqueuedServiceDetails;
import com.cyecize.ioc.models.ServiceDetails;

import java.util.Collection;

public interface DependencyResolveService {

    void init(Collection<ServiceDetails> mappedServices);

    void checkDependencies(Collection<EnqueuedServiceDetails> enqueuedServiceDetails) throws ServiceInstantiationException;

    void addDependency(EnqueuedServiceDetails enqueuedServiceDetails, ServiceDetails serviceDetails);

    boolean isServiceResolved(EnqueuedServiceDetails enqueuedServiceDetails);

    boolean isDependencyRequired(EnqueuedServiceDetails enqueuedServiceDetails, ServiceDetails serviceDetails);
}
