package com.cyecize.ioc.services;

import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.exceptions.AlreadyInitializedException;
import com.cyecize.ioc.models.ServiceBeanDetails;
import com.cyecize.ioc.models.ServiceDetails;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Container for all services and beans.
 * <p>
 * Contains functionality for managing the application context
 * by reloading or accessing certain services.
 */
public class DependencyContainerImpl implements DependencyContainer {

    private static final String ALREADY_INITIALIZED_MSG = "Dependency container already initialized.";

    private static final String SERVICE_NOT_FOUND_FORMAT = "Service \"%s\" was not found.";

    private final Map<Class<?>, ServiceDetails> cachedServices;

    private final Map<Class<?>, Collection<ServiceDetails>> cachedImplementations;

    private final Map<Class<? extends Annotation>, Collection<ServiceDetails>> cachedServicesByAnnotation;

    private boolean isInit;

    private Collection<Class<?>> allLocatedClasses;

    private Collection<ServiceDetails> servicesAndBeans;

    private ObjectInstantiationService instantiationService;

    public DependencyContainerImpl() {
        this.cachedServices = new HashMap<>();
        this.cachedImplementations = new HashMap<>();
        this.cachedServicesByAnnotation = new HashMap<>();
        this.isInit = false;
    }

    @Override
    public void init(Collection<Class<?>> locatedClasses, Collection<ServiceDetails> servicesAndBeans, ObjectInstantiationService instantiationService) throws AlreadyInitializedException {
        if (this.isInit) {
            throw new AlreadyInitializedException(ALREADY_INITIALIZED_MSG);
        }

        this.allLocatedClasses = locatedClasses;
        this.servicesAndBeans = servicesAndBeans;
        this.instantiationService = instantiationService;

        this.isInit = true;
    }

    /**
     * Creates a new instance for a given service and destroys the current one.
     * <p>
     *
     * @param serviceDetails - the given service.
     */
    @Override
    public void reload(ServiceDetails serviceDetails) {
        this.instantiationService.destroyInstance(serviceDetails);
        this.handleReload(serviceDetails);
    }

    @Override
    public void reload(Class<?> serviceType) {
        final ServiceDetails serviceDetails = this.getServiceDetails(serviceType);
        if (serviceDetails == null) {
            throw new IllegalArgumentException(String.format(SERVICE_NOT_FOUND_FORMAT, serviceType));
        }

        this.reload(serviceDetails);
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
            ServiceBeanDetails serviceBeanDetails = (ServiceBeanDetails) serviceDetails;
            this.instantiationService.createBeanInstance(serviceBeanDetails);

            if (!serviceBeanDetails.hasProxyInstance()) {
                //Since bean has no proxy, reload all dependant classes.
                for (ServiceDetails dependantService : serviceDetails.getDependantServices()) {
                    this.reload(dependantService);
                }
            }
        } else {
            this.instantiationService.createInstance(
                    serviceDetails,
                    this.collectDependencies(serviceDetails),
                    this.collectAutowiredFieldsDependencies(serviceDetails)
            );
        }
    }

    /**
     * Gets instances of all required dependencies for a given service.
     *
     * @param serviceDetails - the given service.
     * @return array of instantiated dependencies.
     */
    private Object[] collectDependencies(ServiceDetails serviceDetails) {
        final Class<?>[] parameterTypes = serviceDetails.getTargetConstructor().getParameterTypes();
        final Object[] dependencyInstances = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            dependencyInstances[i] = this.getService(parameterTypes[i]);
        }

        return dependencyInstances;
    }

    /**
     * Gets instances of all {@link Autowired} annotated dependencies for a given service.
     *
     * @param serviceDetails - the given service.
     * @return array of instantiated dependencies.
     */
    private Object[] collectAutowiredFieldsDependencies(ServiceDetails serviceDetails) {
        final Field[] autowireAnnotatedFields = serviceDetails.getAutowireAnnotatedFields();
        final Object[] instances = new Object[autowireAnnotatedFields.length];

        for (int i = 0; i < autowireAnnotatedFields.length; i++) {
            instances[i] = this.getService(autowireAnnotatedFields[i].getType());
        }

        return instances;
    }

    /**
     * Replaces instance of a service with a new provided one.
     *
     * @param service new instance of a given service
     */
    @Override
    public void update(Object service) {
        this.update(service.getClass(), service);
    }

    /**
     * Replaces instance of a service with a new provided one.
     *
     * @param serviceType     given service type.
     * @param serviceInstance new instance of a given service.
     */
    @Override
    public void update(Class<?> serviceType, Object serviceInstance) {
        final ServiceDetails serviceDetails = this.getServiceDetails(serviceType);
        if (serviceDetails == null) {
            throw new IllegalArgumentException(String.format(SERVICE_NOT_FOUND_FORMAT, serviceType.getName()));
        }

        this.instantiationService.destroyInstance(serviceDetails);
        serviceDetails.setInstance(serviceInstance);
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
        final ServiceDetails serviceDetails = this.getServiceDetails(serviceType);

        if (serviceDetails != null) {
            return (T) serviceDetails.getProxyInstance();
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
        if (this.cachedServices.containsKey(serviceType)) {
            return this.cachedServices.get(serviceType);
        }

        final ServiceDetails serviceDetails = this.servicesAndBeans.stream()
                .filter(sd -> serviceType.isAssignableFrom(sd.getProxyInstance().getClass()) || serviceType.isAssignableFrom(sd.getServiceType()))
                .findFirst().orElse(null);

        if (serviceDetails != null) {
            this.cachedServices.put(serviceType, serviceDetails);
        }

        return serviceDetails;
    }

    /**
     * @return a collection of all classes that were found in the application
     * including even classes that are not annotated with any annotation.
     */
    @Override
    public Collection<Class<?>> getAllScannedClasses() {
        return this.allLocatedClasses;
    }

    /**
     * @param serviceType given interface.
     * @return collection of service details that implement the given interface.
     */
    @Override
    public Collection<ServiceDetails> getImplementations(Class<?> serviceType) {
        if (this.cachedImplementations.containsKey(serviceType)) {
            return this.cachedImplementations.get(serviceType);
        }

        final List<ServiceDetails> implementations = this.servicesAndBeans.stream()
                .filter(sd -> serviceType.isAssignableFrom(sd.getServiceType()))
                .collect(Collectors.toList());

        this.cachedImplementations.put(serviceType, implementations);

        return implementations;
    }

    /**
     * Gets all services that are mapped with a given annotation.
     *
     * @param annotationType the given annotation.
     */
    @Override
    public Collection<ServiceDetails> getServicesByAnnotation(Class<? extends Annotation> annotationType) {
        if (this.cachedServicesByAnnotation.containsKey(annotationType)) {
            return this.cachedServicesByAnnotation.get(annotationType);
        }

        final List<ServiceDetails> serviceDetailsByAnnotation = this.servicesAndBeans.stream()
                .filter(sd -> sd.getAnnotation() != null && sd.getAnnotation().annotationType() == annotationType)
                .collect(Collectors.toList());

        this.cachedServicesByAnnotation.put(annotationType, serviceDetailsByAnnotation);

        return serviceDetailsByAnnotation;
    }

    /**
     * Gets only the instances of all services.
     */
    @Override
    public Collection<ServiceDetails> getAllServices() {
        return this.servicesAndBeans;
    }
}
