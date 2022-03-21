package com.cyecize.ioc.services;

import com.cyecize.ioc.models.EnqueuedServiceDetails;
import com.cyecize.ioc.models.ServiceDetails;

import java.util.Collection;
import java.util.List;

public interface DependencyResolveService {
    List<EnqueuedServiceDetails> resolveDependencies(Collection<ServiceDetails> serviceDetails);
}
