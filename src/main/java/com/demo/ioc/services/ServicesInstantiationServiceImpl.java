package com.demo.ioc.services;

import com.demo.ioc.config.configurations.InstantiationConfiguration;
import com.demo.ioc.exceptions.ServiceInstantiationException;
import com.demo.ioc.models.EnqueuedServiceDetails;
import com.demo.ioc.models.ServiceBeanDetails;
import com.demo.ioc.models.ServiceDetails;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link ServicesInstantiationService} implementation.
 * <p>
 * Responsible for creating the initial instances or all services and beans.
 */
public class ServicesInstantiationServiceImpl implements ServicesInstantiationService {

    private static final String MAX_NUMBER_OF_ALLOWED_ITERATIONS_REACHED = "Maximum number of allowed iterations was reached '%s'.";

    private static final String COULD_NOT_FIND_CONSTRUCTOR_PARAM_MSG = "Could not create instance of '%s'. Parameter '%s' implementation was not found";

    /**
     * Configuration containing the maximum number or allowed iterations.
     */
    private final InstantiationConfiguration configuration;

    private final ObjectInstantiationService instantiationService;

    /**
     * The storage for all services that are waiting to the instantiated.
     */
    private final LinkedList<EnqueuedServiceDetails> enqueuedServiceDetails;

    /**
     * Contains all available types that will be loaded from this service.
     * This includes all services and all beans.
     */
    private final List<Class<?>> allAvailableClasses;

    /**
     * Contains services and beans that have been loaded.
     */
    private final List<ServiceDetails> instantiatedServices;

    public ServicesInstantiationServiceImpl(InstantiationConfiguration configuration, ObjectInstantiationService instantiationService) {
        this.configuration = configuration;
        this.instantiationService = instantiationService;
        this.enqueuedServiceDetails = new LinkedList<>();
        this.allAvailableClasses = new ArrayList<>();
        this.instantiatedServices = new ArrayList<>();
    }

    /**
     * Starts looping and for each cycle gets the first element in the enqueuedServiceDetails since they are ordered
     * by the number of constructor params in ASC order.
     * <p>
     * If the {@link EnqueuedServiceDetails} is resolved (all of its dependencies have instances or there are no params)
     * then the service is being instantiated, registered and its beans are also being registered.
     * Otherwise the element is added back to the enqueuedServiceDetails at the last position.
     *
     * @param mappedServices provided services and their details.
     * @return list of all instantiated services and beans.
     * @throws ServiceInstantiationException if maximum number of iterations is reached
     *                                       One iteration is added only of a service has not been instantiated in this cycle.
     */
    @Override
    public List<ServiceDetails> instantiateServicesAndBeans(Set<ServiceDetails> mappedServices) throws ServiceInstantiationException {
        this.init(mappedServices);
        this.checkForMissingServices(mappedServices);

        int counter = 0;
        int maxNumberOfIterations = this.configuration.getMaximumAllowedIterations();
        while (!this.enqueuedServiceDetails.isEmpty()) {
            if (counter > maxNumberOfIterations) {
                throw new ServiceInstantiationException(String.format(MAX_NUMBER_OF_ALLOWED_ITERATIONS_REACHED, maxNumberOfIterations));
            }

            EnqueuedServiceDetails enqueuedServiceDetails = this.enqueuedServiceDetails.removeFirst();

            if (enqueuedServiceDetails.isResolved()) {
                ServiceDetails serviceDetails = enqueuedServiceDetails.getServiceDetails();
                Object[] dependencyInstances = enqueuedServiceDetails.getDependencyInstances();

                this.instantiationService.createInstance(serviceDetails, dependencyInstances);
                this.registerInstantiatedService(serviceDetails);
                this.registerBeans(serviceDetails);
            } else {
                this.enqueuedServiceDetails.addLast(enqueuedServiceDetails);
                counter++;
            }
        }

        return this.instantiatedServices;
    }

    /**
     * Iterates all bean methods for the given service.
     * Creates {@link ServiceBeanDetails} and then creates instance of the bean.
     * Finally calls registerInstantiatedService so that enqueued services are aware of the
     * newly created bean.
     *
     * @param serviceDetails given service.
     */
    private void registerBeans(ServiceDetails serviceDetails) {
        for (Method beanMethod : serviceDetails.getBeans()) {
            ServiceBeanDetails beanDetails = new ServiceBeanDetails(beanMethod.getReturnType(), beanMethod, serviceDetails);
            this.instantiationService.createBeanInstance(beanDetails);
            this.registerInstantiatedService(beanDetails);
        }
    }

    /**
     * Adds the newly created service to the list of instantiated services instantiatedServices.
     * Iterated all enqueued services and if one of them relies on that services, adds its instance
     * to them so they can get resolved.
     *
     * @param newlyCreatedService - the created service.
     */
    private void registerInstantiatedService(ServiceDetails newlyCreatedService) {
        if (!(newlyCreatedService instanceof ServiceBeanDetails)) {
            this.updatedDependantServices(newlyCreatedService);
        }

        this.instantiatedServices.add(newlyCreatedService);

        for (EnqueuedServiceDetails enqueuedService : this.enqueuedServiceDetails) {
            if (enqueuedService.isDependencyRequired(newlyCreatedService.getServiceType())) {
                enqueuedService.addDependencyInstance(newlyCreatedService.getInstance());
            }
        }
    }

    /**
     * Gets all dependencies of the given new service.
     * <p>
     * For each dependency, in the form of {@link ServiceDetails}
     * adds itself to its dependant services list.
     *
     * @param newService - the newly created service.
     */
    private void updatedDependantServices(ServiceDetails newService) {
        for (Class<?> parameterType : newService.getTargetConstructor().getParameterTypes()) {
            for (ServiceDetails serviceDetails : this.instantiatedServices) {
                if (parameterType.isAssignableFrom(serviceDetails.getServiceType())) {
                    serviceDetails.addDependantService(newService);
                }
            }
        }
    }

    /**
     * Checks if the client has a service that will never be instantiated because
     * it has a dependency that is not present in the application context.
     *
     * @param mappedServices set of all mapped services.
     * @throws ServiceInstantiationException if a service has a dependency that is not
     *                                       present in the application context.
     */
    private void checkForMissingServices(Set<ServiceDetails> mappedServices) throws ServiceInstantiationException {
        for (ServiceDetails serviceDetails : mappedServices) {
            for (Class<?> parameterType : serviceDetails.getTargetConstructor().getParameterTypes()) {
                if (!this.isAssignableTypePresent(parameterType)) {
                    throw new ServiceInstantiationException(
                            String.format(COULD_NOT_FIND_CONSTRUCTOR_PARAM_MSG,
                                    serviceDetails.getServiceType().getName(),
                                    parameterType.getName()
                            )
                    );
                }
            }
        }
    }

    /**
     * @param cls given type.
     * @return true if allAvailableClasses contains a type
     * that is compatible with the given type.
     */
    private boolean isAssignableTypePresent(Class<?> cls) {
        for (Class<?> serviceType : this.allAvailableClasses) {
            if (cls.isAssignableFrom(serviceType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds each service to the enqueuedServiceDetails.
     * Adds each service type to allAvailableClasses.
     * Adds all beans that can be loaded from each service
     * to allAvailableClasses.
     *
     * @param mappedServices set of mapped services and their information.
     */
    private void init(Set<ServiceDetails> mappedServices) {
        this.enqueuedServiceDetails.clear();
        this.allAvailableClasses.clear();
        this.instantiatedServices.clear();

        for (ServiceDetails serviceDetails : mappedServices) {
            this.enqueuedServiceDetails.add(new EnqueuedServiceDetails(serviceDetails));
            this.allAvailableClasses.add(serviceDetails.getServiceType());
            this.allAvailableClasses.addAll(Arrays.stream(serviceDetails.getBeans())
                    .map(Method::getReturnType)
                    .collect(Collectors.toList())
            );
        }
    }
}
