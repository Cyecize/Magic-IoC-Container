package com.cyecize.ioc.config.configurations;

import com.cyecize.ioc.constants.Constants;
import com.cyecize.ioc.config.BaseSubConfiguration;
import com.cyecize.ioc.config.MagicConfiguration;
import com.cyecize.ioc.handlers.DependencyResolver;
import com.cyecize.ioc.models.ServiceDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class InstantiationConfiguration extends BaseSubConfiguration {

    private int maximumAllowedIterations;

    private final Collection<ServiceDetails> providedServices;

    private final Set<DependencyResolver> dependencyResolvers;

    public InstantiationConfiguration(MagicConfiguration parentConfig) {
        super(parentConfig);
        this.providedServices = new ArrayList<>();
        this.maximumAllowedIterations = Constants.MAX_NUMBER_OF_INSTANTIATION_ITERATIONS;
        this.dependencyResolvers = new HashSet<>();
    }

    public InstantiationConfiguration setMaximumNumberOfAllowedIterations(int num) {
        this.maximumAllowedIterations = num;
        return this;
    }

    public int getMaximumAllowedIterations() {
        return this.maximumAllowedIterations;
    }

    public InstantiationConfiguration addProvidedServices(Collection<ServiceDetails> serviceDetails) {
        this.providedServices.addAll(serviceDetails);
        return this;
    }

    public InstantiationConfiguration addDependencyResolver(DependencyResolver dependencyResolver) {
        this.dependencyResolvers.add(dependencyResolver);
        return this;
    }

    public Collection<ServiceDetails> getProvidedServices() {
        return this.providedServices;
    }

    public Set<DependencyResolver> getDependencyResolvers() {
        return this.dependencyResolvers;
    }
}
