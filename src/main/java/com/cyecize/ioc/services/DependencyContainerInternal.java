package com.cyecize.ioc.services;

import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.enums.ScopeType;
import com.cyecize.ioc.exceptions.AlreadyInitializedException;
import com.cyecize.ioc.middleware.DependencyResolver;
import com.cyecize.ioc.models.DependencyParam;
import com.cyecize.ioc.models.ServiceBeanDetails;
import com.cyecize.ioc.models.ServiceDetails;
import com.cyecize.ioc.utils.ServiceCompatibilityUtils;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Container for all services and beans.
 * <p>
 * Contains functionality for managing the application context
 * by reloading or accessing certain services.
 */
public class DependencyContainerInternal implements DependencyContainer {

    private static final String ALREADY_INITIALIZED_MSG = "Dependency container already initialized.";

    private static final String SERVICE_NOT_FOUND_FORMAT = "Service \"%s\" was not found.";

    private boolean isInit;

    private Collection<Class<?>> allLocatedClasses;

    private Collection<ServiceDetails> servicesAndBeans;

    private ObjectInstantiationService instantiationService;

    public DependencyContainerInternal() {
        this.isInit = false;
    }

    @Override
    public void init(Collection<Class<?>> locatedClasses, Collection<ServiceDetails> servicesAndBeans,
                     ObjectInstantiationService instantiationService) throws AlreadyInitializedException {
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
        final Object newInstance = this.getNewInstance(serviceDetails.getServiceType(), serviceDetails.getInstanceName());
        serviceDetails.setInstance(newInstance);
    }

    @Override
    public void reload(Class<?> serviceType) {
        final ServiceDetails serviceDetails = this.findServiceDetails(serviceType, null);
        if (serviceDetails == null) {
            throw new IllegalArgumentException(String.format(SERVICE_NOT_FOUND_FORMAT, serviceType));
        }

        this.reload(serviceDetails);
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
        this.update(serviceType, serviceInstance, true);
    }

    @Override
    public void update(Class<?> serviceType, Object serviceInstance, boolean destroyOldInstance) {
        final ServiceDetails serviceDetails = this.findServiceDetails(serviceType, null);
        if (serviceDetails == null) {
            throw new IllegalArgumentException(String.format(SERVICE_NOT_FOUND_FORMAT, serviceType.getName()));
        }

        if (destroyOldInstance) {
            this.instantiationService.destroyInstance(serviceDetails);
        }

        serviceDetails.setInstance(serviceInstance);
    }

    /**
     * Gets service instance for a given type.
     *
     * @param serviceType the given type.
     * @param <T>         generic type.
     * @return instance of the required service or null.
     */
    @Override
    public <T> T getService(Class<T> serviceType) {
        return this.getService(serviceType, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<?> serviceType, String instanceName) {
        final ServiceDetails serviceDetails = this.getServiceDetails(serviceType, instanceName);

        if (serviceDetails != null) {
            return (T) serviceDetails.getInstance();
        }

        if (serviceType.isAssignableFrom(this.getClass())) {
            return (T) this;
        }

        return null;
    }

    @Override
    public <T> T getNewInstance(Class<?> serviceType) {
        return this.getNewInstance(serviceType, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getNewInstance(Class<?> serviceType, String instanceName) {
        final ServiceDetails serviceDetails = this.findServiceDetails(serviceType, instanceName);

        if (serviceDetails == null) {
            throw new IllegalArgumentException(String.format(SERVICE_NOT_FOUND_FORMAT, serviceType.getName()));
        }

        final Object oldInstance = serviceDetails.getActualInstance();

        if (serviceDetails instanceof ServiceBeanDetails) {
            final ServiceBeanDetails serviceBeanDetails = (ServiceBeanDetails) serviceDetails;
            this.instantiationService.createBeanInstance(serviceBeanDetails);
        } else {
            this.instantiationService.createInstance(
                    serviceDetails,
                    this.collectDependencies(serviceDetails),
                    this.collectAutowiredFieldsDependencies(serviceDetails)
            );
        }

        final Object newInstance = serviceDetails.getActualInstance();
        serviceDetails.setInstance(oldInstance);

        return (T) newInstance;
    }

    /**
     * Gets service details object for a given service type.
     *
     * @param serviceType - the given service type.
     * @return service details or null.
     */
    @Override
    public ServiceDetails getServiceDetails(Class<?> serviceType) {
        return this.getServiceDetails(serviceType, null);
    }

    /**
     * Finds a service details instance for a given service type.
     * Created new instance of that service if the service is with PROTOTYPE scope.
     *
     * @param serviceType  - given service type.
     * @param instanceName - given instance name.
     * @return service details if found or null.
     */
    @Override
    public ServiceDetails getServiceDetails(Class<?> serviceType, String instanceName) {
        final ServiceDetails serviceDetails = this.findServiceDetails(serviceType, instanceName);

        if (serviceDetails != null) {
            if (serviceDetails.getScopeType() == ScopeType.PROTOTYPE) {
                serviceDetails.setInstance(this.getNewInstance(serviceType, instanceName));
            }
        }

        return serviceDetails;
    }

    /**
     * @param serviceType  - given service type.
     * @param instanceName - given instance name.
     * @return service details if found or null.
     */
    private ServiceDetails findServiceDetails(Class<?> serviceType, String instanceName) {
        return this.servicesAndBeans.stream()
                .filter(sd -> ServiceCompatibilityUtils.isServiceCompatible(sd, serviceType, instanceName))
                .findFirst().orElse(null);
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
        return this.servicesAndBeans.stream()
                .filter(sd -> serviceType.isAssignableFrom(sd.getServiceType()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all services that are mapped with a given annotation.
     *
     * @param annotationType the given annotation.
     */
    @Override
    public Collection<ServiceDetails> getServicesByAnnotation(Class<? extends Annotation> annotationType) {
        return this.servicesAndBeans.stream()
                .filter(sd -> sd.getAnnotation() != null && sd.getAnnotation().annotationType() == annotationType)
                .collect(Collectors.toList());
    }

    /**
     * Gets all services.
     */
    @Override
    public Collection<ServiceDetails> getAllServices() {
        return this.servicesAndBeans;
    }

    /**
     * Gets instances of all required dependencies for a given service.
     *
     * @param serviceDetails - the given service.
     * @return array of instantiated dependencies.
     */
    private Object[] collectDependencies(ServiceDetails serviceDetails) {
        return this.getInstances(serviceDetails.getResolvedConstructorParams());
    }

    /**
     * Gets instances of all {@link Autowired} annotated dependencies for a given service.
     *
     * @param serviceDetails - the given service.
     * @return array of instantiated dependencies.
     */
    private Object[] collectAutowiredFieldsDependencies(ServiceDetails serviceDetails) {
        return this.getInstances(serviceDetails.getResolvedFields());
    }

    /**
     * Iterates array of dependencies and finds their instance.
     * <p>
     * If a dependency has a {@link DependencyResolver} it will be used to obtain the value.
     *
     * @param dependencyParams - list of dependencies.
     * @return - array of object instances in the same order as received.
     */
    private Object[] getInstances(List<DependencyParam> dependencyParams) {
        final Object[] instances = new Object[dependencyParams.size()];

        for (int i = 0; i < dependencyParams.size(); i++) {
            final DependencyParam dependencyParam = dependencyParams.get(i);

            if (dependencyParam.getDependencyResolver() != null) {
                instances[i] = dependencyParam.getDependencyResolver().resolve(dependencyParam);
                continue;
            }

            instances[i] = this.getService(dependencyParam.getDependencyType(), dependencyParam.getInstanceName());
        }

        return instances;
    }
}
