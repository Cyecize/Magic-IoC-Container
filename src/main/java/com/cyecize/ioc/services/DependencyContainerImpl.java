package com.cyecize.ioc.services;

import com.cyecize.ioc.exceptions.AlreadyInitializedException;
import com.cyecize.ioc.models.ServiceBeanDetails;
import com.cyecize.ioc.models.ServiceDetails;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Container for all services and beans.
 * <p>
 * Contains functionality for managing the application context
 * by reloading or accessing certain services.
 */
public class DependencyContainerImpl implements DependencyContainer {

    private static final String ALREADY_INITIALIZED_MSG = "Dependency container already initialized.";

    private boolean isInit;

    private List<ServiceDetails> servicesAndBeans;

    private ObjectInstantiationService instantiationService;

    public DependencyContainerImpl() {
        this.isInit = false;
    }

    @Override
    public void init(List<ServiceDetails> servicesAndBeans, ObjectInstantiationService instantiationService) throws AlreadyInitializedException {
        if (this.isInit) {
            throw new AlreadyInitializedException(ALREADY_INITIALIZED_MSG);
        }

        this.servicesAndBeans = servicesAndBeans;
        this.instantiationService = instantiationService;

        this.isInit = true;
    }

    /**
     * Creates a new instance for a given service and destroys the current one.
     * <p>
     * If reloadDependantServices flag is true, gets all services that depend of this one and
     * reloads them with the new instance of that given service.
     *
     * @param serviceDetails          - the given service.
     * @param reloadDependantServices - flag for reloading all services that depend of the given one.
     */
    @Override
    public void reload(ServiceDetails serviceDetails, boolean reloadDependantServices) {
        this.instantiationService.destroyInstance(serviceDetails);
        this.handleReload(serviceDetails);

        if (reloadDependantServices) {
            for (ServiceDetails dependantService : serviceDetails.getDependantServices()) {
                this.reload(dependantService, reloadDependantServices);
            }
        }
    }

    /**
     * Handles different types of service.
     * <p>
     * If the service is bean, it does not have a constructor, but an origin method.
     *
     * @param serviceDetails - target service.
     */
    private void handleReload(ServiceDetails serviceDetails) {
        if (serviceDetails instanceof ServiceBeanDetails) {
            this.instantiationService.createBeanInstance((ServiceBeanDetails) serviceDetails);
        } else {
            this.instantiationService.createInstance(serviceDetails, this.collectDependencies(serviceDetails));
        }
    }

    /**
     * Gets instances of all required dependencies for a given service.
     *
     * @param serviceDetails - the given service.
     * @return array of instantiated dependencies.
     */
    private Object[] collectDependencies(ServiceDetails serviceDetails) {
        Class<?>[] parameterTypes = serviceDetails.getTargetConstructor().getParameterTypes();
        Object[] dependencyInstances = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            dependencyInstances[i] = this.getService(parameterTypes[i]);
        }

        return dependencyInstances;
    }

    /**
     * Reloads a service and returns the new instance.
     * Does not reload any depending services on that given one.
     *
     * @param service service instance.
     * @param <T>     service type.
     * @return refreshed service instance.
     */
    @Override
    public <T> T reload(T service) {
        return this.reload(service, false);
    }

    /**
     * Reloads a service and returns the new instance.
     * Has the option to reload every service that depends on the given one
     * recursively.
     *
     * @param service                 service instance.
     * @param <T>                     service type.
     * @param reloadDependantServices - flag for reloading all services that depend of the given one.
     * @return refreshed service instance.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T reload(T service, boolean reloadDependantServices) {
        ServiceDetails serviceDetails = this.getServiceDetails(service.getClass());

        if (serviceDetails == null) {
            return null;
        }

        this.reload(serviceDetails, reloadDependantServices);

        return (T) serviceDetails.getInstance();
    }

    /**
     * Gets service instance for a given type.
     *
     * @param serviceType the given type.
     * @param <T>         generic type.
     * @return instance of the required service or null.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getService(Class<T> serviceType) {
        ServiceDetails serviceDetails = this.getServiceDetails(serviceType);

        if (serviceDetails != null) {
            return (T) serviceDetails.getInstance();
        }

        return null;
    }

    /**
     * Gets service details object for a given service type.
     *
     * @param serviceType - the given service type.
     * @return service details or null.
     */
    @Override
    public ServiceDetails getServiceDetails(Class<?> serviceType) {
        return this.servicesAndBeans.stream()
                .filter(sd -> serviceType.isAssignableFrom(sd.getServiceType()))
                .findFirst().orElse(null);
    }

    /**
     * Gets all services that are mapped with a given annotation.
     *
     * @param annotationType the given annotation.
     */
    @Override
    public List<ServiceDetails> getServicesByAnnotation(Class<? extends Annotation> annotationType) {
        return this.servicesAndBeans.stream()
                .filter(sd -> sd.getAnnotation() != null && sd.getAnnotation().annotationType() == annotationType)
                .collect(Collectors.toList());
    }

    /**
     * Gets only the instances of all services.
     */
    @Override
    public List<Object> getAllServices() {
        return this.servicesAndBeans.stream()
                .map(ServiceDetails::getInstance)
                .collect(Collectors.toList());
    }

    /**
     * Gets all services.
     */
    @Override
    public List<ServiceDetails> getAllServiceDetails() {
        return Collections.unmodifiableList(this.servicesAndBeans);
    }
}
