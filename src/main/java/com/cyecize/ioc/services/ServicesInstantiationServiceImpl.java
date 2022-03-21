package com.cyecize.ioc.services;

import com.cyecize.ioc.enums.ScopeType;
import com.cyecize.ioc.models.EnqueuedServiceDetails;
import com.cyecize.ioc.models.ServiceBeanDetails;
import com.cyecize.ioc.models.ServiceDetails;
import com.cyecize.ioc.utils.ObjectInstantiationUtils;
import com.cyecize.ioc.utils.ProxyUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * {@link ServicesInstantiationService} implementation.
 * <p>
 * Responsible for creating the initial instances or all services and beans.
 */
public class ServicesInstantiationServiceImpl implements ServicesInstantiationService {

    private final DependencyResolveService dependencyResolveService;

    public ServicesInstantiationServiceImpl(DependencyResolveService dependencyResolveService) {
        this.dependencyResolveService = dependencyResolveService;
    }

    /**
     * @param mappedServices -
     * @return - collection of all instantiated services and beans.
     */
    @Override
    public Collection<ServiceDetails> instantiateServicesAndBeans(Set<ServiceDetails> mappedServices) {
        final List<EnqueuedServiceDetails> enqueuedServiceDetails = this.dependencyResolveService
                .resolveDependencies(mappedServices);

        for (EnqueuedServiceDetails service : enqueuedServiceDetails) {
            this.instantiateService(service);
        }

        final List<ServiceDetails> allServicesAndBeans = new ArrayList<>();
        mappedServices.forEach(serviceDetails -> {
            allServicesAndBeans.add(serviceDetails);
            allServicesAndBeans.addAll(serviceDetails.getBeans());
        });

        return allServicesAndBeans;
    }

    private void instantiateService(EnqueuedServiceDetails enqueuedServiceDetails) {
        final ServiceDetails serviceDetails = enqueuedServiceDetails.getServiceDetails();
        final Object[] constructorInstances = enqueuedServiceDetails.getConstructorInstances();

        //In case a service provided by the config already came with an instance.
        if (enqueuedServiceDetails.getServiceDetails().getInstance() == null) {
            ObjectInstantiationUtils.createInstance(
                    serviceDetails,
                    constructorInstances,
                    enqueuedServiceDetails.getFieldInstances()
            );
        }

        if (serviceDetails.getScopeType() == ScopeType.PROXY) {
            ProxyUtils.createProxyInstance(serviceDetails, enqueuedServiceDetails.getConstructorInstances());
        }

        this.registerResolvedDependencies(enqueuedServiceDetails);
        this.registerBeans(serviceDetails);
    }

    /**
     * Iterates all bean methods for the given service and creates instance of the bean.
     *
     * @param serviceDetails given service.
     */
    private void registerBeans(ServiceDetails serviceDetails) {
        for (ServiceBeanDetails beanDetails : serviceDetails.getBeans()) {
            ObjectInstantiationUtils.createBeanInstance(beanDetails);
            if (beanDetails.getScopeType() == ScopeType.PROXY) {
                ProxyUtils.createBeanProxyInstance(beanDetails);
            }
        }
    }

    private void registerResolvedDependencies(EnqueuedServiceDetails enqueuedServiceDetails) {
        final ServiceDetails serviceDetails = enqueuedServiceDetails.getServiceDetails();

        serviceDetails.setResolvedConstructorParams(enqueuedServiceDetails.getConstructorParams());
        serviceDetails.setResolvedFields(enqueuedServiceDetails.getFieldDependencies());
    }
}
