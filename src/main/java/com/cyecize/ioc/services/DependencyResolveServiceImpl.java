package com.cyecize.ioc.services;

import com.cyecize.ioc.annotations.Nullable;
import com.cyecize.ioc.annotations.Qualifier;
import com.cyecize.ioc.config.configurations.InstantiationConfiguration;
import com.cyecize.ioc.exceptions.CircularDependencyException;
import com.cyecize.ioc.exceptions.ServiceInstantiationException;
import com.cyecize.ioc.handlers.DependencyResolver;
import com.cyecize.ioc.models.DependencyParam;
import com.cyecize.ioc.models.DependencyParamCollection;
import com.cyecize.ioc.models.EnqueuedServiceDetails;
import com.cyecize.ioc.models.MethodAspectHandlerDto;
import com.cyecize.ioc.models.ResolvedServiceDto;
import com.cyecize.ioc.models.ServiceDetails;
import com.cyecize.ioc.utils.AliasFinder;
import com.cyecize.ioc.utils.DependencyParamUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyResolveServiceImpl implements DependencyResolveService {
    private final InstantiationConfiguration configuration;

    public DependencyResolveServiceImpl(InstantiationConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Recursively iterate all services and resolve their dependencies by
     * creating {@link DependencyParam} which is directly linked by the {@link ServiceDetails} of a given dependency.
     * This will ensure that whenever the dependency is instantiated, the dependant service will instantly have access
     * to the instance.
     *
     * @param serviceDetails -
     * @return services sorted by their dependencies
     * @throws ServiceInstantiationException - if dependency is missing
     * @throws CircularDependencyException   - if resolution is impossible due to circular dependency
     */
    @Override
    public List<EnqueuedServiceDetails> resolveDependencies(Collection<ServiceDetails> serviceDetails) {
        final List<EnqueuedServiceDetails> resolvedDependencies = new ArrayList<>();

        final List<ServiceDetails> allAvailableServices = new ArrayList<>(serviceDetails);
        allAvailableServices.addAll(this.configuration.getProvidedServices());

        for (ServiceDetails service : allAvailableServices) {
            this.resolveDependency(service, resolvedDependencies, allAvailableServices, new LinkedList<>());
        }

        return resolvedDependencies;
    }

    /**
     * Checks aspects, constructor and field parameters for a given service and if there are any,
     * recursively traverses them so that a dependency order is achieved.
     * Every {@link DependencyParam} will be linked with a compatible {@link ServiceDetails}.
     * Also, checks for circular is missing dependencies.
     *
     * @param service              -
     * @param resolvedDependencies -
     * @param serviceTrace         -
     */
    private void resolveDependency(ServiceDetails service,
                                   List<EnqueuedServiceDetails> resolvedDependencies,
                                   List<ServiceDetails> allAvailableServices,
                                   LinkedList<ServiceDetails> serviceTrace) {
        this.checkForCyclicDependency(service, serviceTrace);
        final EnqueuedServiceDetails enqueuedServiceDetails = new EnqueuedServiceDetails(service);
        if (resolvedDependencies.contains(enqueuedServiceDetails)) {
            return;
        }

        serviceTrace.addFirst(service);
        final Set<MethodAspectHandlerDto> aspects = service.getMethodAspectHandlers()
                .values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        for (MethodAspectHandlerDto aspect : aspects) {
            this.resolveDependency(aspect.getServiceDetails(), resolvedDependencies, allAvailableServices, serviceTrace);
        }

        final List<DependencyParam> dependencyParams = new ArrayList<>() {{
            addAll(enqueuedServiceDetails.getConstructorParams());
            addAll(enqueuedServiceDetails.getFieldDependencies());
        }};

        for (DependencyParam dependencyParam : dependencyParams) {
            final List<ServiceDetails> servicesToResolve;
            try {
                servicesToResolve = this.resolveParameter(dependencyParam, allAvailableServices);
            } catch (Exception ex) {
                throw new ServiceInstantiationException(String.format(
                        "Error while resolving dependencies for service '%s'.", service.getServiceType()
                ), ex);
            }

            for (ServiceDetails serviceToResolve : servicesToResolve) {
                this.resolveDependency(serviceToResolve, resolvedDependencies, allAvailableServices, serviceTrace);
            }
        }

        serviceTrace.removeFirst();
        resolvedDependencies.add(enqueuedServiceDetails);
    }

    /**
     * Attempts to resolve dependency parameter by named instance, compatible class or dependency resolver.
     *
     * @param dependencyParam      -
     * @param allAvailableServices -
     * @return list of services that the given param relies on
     * @throws ServiceInstantiationException - if none of the options found a match and the parameter is not {@link Nullable}
     */
    private List<ServiceDetails> resolveParameter(DependencyParam dependencyParam,
                                                  List<ServiceDetails> allAvailableServices) {
        final Class<?> dependencyType = dependencyParam.getDependencyType();

        if (AliasFinder.isAnnotationPresent(dependencyParam.getAnnotations(), Nullable.class)) {
            dependencyParam.setRequired(false);
        }

        if (dependencyParam.getInstanceName() != null) {
            final ResolvedServiceDto resolvedNamedService = DependencyParamUtils.getNamedInstanceService(
                    dependencyType,
                    dependencyParam.getInstanceName(),
                    allAvailableServices
            );

            if (resolvedNamedService != null) {
                dependencyParam.setServiceDetails(resolvedNamedService.getActualService());
                return List.of(resolvedNamedService.getProducerService());
            }

            if (dependencyParam.isRequired()) {
                throw new ServiceInstantiationException(String.format(
                        "Could not create instance of '%s'. Qualifier '%s' was not found.",
                        dependencyType,
                        dependencyParam.getInstanceName()
                ));
            }
        }

        final List<ServiceDetails> resolvedServices;
        if (dependencyParam instanceof DependencyParamCollection) {
            resolvedServices = this.loadCompatibleServiceDetails(
                    (DependencyParamCollection) dependencyParam,
                    allAvailableServices
            );
        } else {
            resolvedServices = this.loadCompatibleServiceDetails(dependencyParam, allAvailableServices);
        }

        if (!resolvedServices.isEmpty()) {
            return resolvedServices;
        }

        final DependencyResolver dependencyResolver = this.getDependencyResolver(dependencyParam);
        if (dependencyResolver != null) {
            //TODO: do not set instance, add support for proxy and singleton dependency resolvers
            dependencyParam.setInstance(dependencyResolver.resolve(dependencyParam));
            dependencyParam.setDependencyResolver(dependencyResolver);
            return List.of();
        }

        if (dependencyParam.isRequired()) {
            throw new ServiceInstantiationException(
                    String.format("Could not create instance of '%s'. Parameter '%s' implementation was not found",
                            dependencyType,
                            dependencyType.getName()
                    )
            );
        }

        return List.of();
    }

    private List<ServiceDetails> loadCompatibleServiceDetails(DependencyParam dependencyParam,
                                                              List<ServiceDetails> allAvailableServices) {
        final List<ResolvedServiceDto> compatibleServices = DependencyParamUtils.findAllCompatibleServices(
                dependencyParam, allAvailableServices
        );

        if (compatibleServices.size() > 1) {
            throw new ServiceInstantiationException(String.format(
                    "Could not create instance of '%s'. "
                            + "There are more than one compatible services: (%s)."
                            + "Please consider using '%s' annotation.",
                    dependencyParam.getDependencyType(),
                    compatibleServices.stream().map(ResolvedServiceDto::getActualService).collect(Collectors.toList()),
                    Qualifier.class.getName()
            ));
        }

        if (compatibleServices.isEmpty()) {
            return List.of();
        }

        dependencyParam.setServiceDetails(compatibleServices.get(0).getActualService());

        return compatibleServices.stream().map(ResolvedServiceDto::getProducerService).collect(Collectors.toList());
    }

    private List<ServiceDetails> loadCompatibleServiceDetails(DependencyParamCollection dependencyParam,
                                                              List<ServiceDetails> allAvailableServices) {
        final List<ResolvedServiceDto> compatibleServices = DependencyParamUtils.findAllCompatibleServices(
                dependencyParam, allAvailableServices
        );

        dependencyParam.setServiceDetails(compatibleServices.stream()
                .map(ResolvedServiceDto::getActualService)
                .collect(Collectors.toList())
        );

        return compatibleServices.stream().map(ResolvedServiceDto::getProducerService).collect(Collectors.toList());
    }

    private DependencyResolver getDependencyResolver(DependencyParam dependencyParam) {
        return this.configuration.getDependencyResolvers().stream()
                .filter(dr -> dr.canResolve(dependencyParam))
                .findFirst().orElse(null);
    }

    private void checkForCyclicDependency(ServiceDetails service, LinkedList<ServiceDetails> serviceTrace) {
        if (serviceTrace.isEmpty()) {
            return;
        }

        if (!serviceTrace.contains(service)) {
            return;
        }

        char arrowDown = '\u2193';
        char arrowUp = '\u2191';

        final StringBuilder sb = new StringBuilder();
        sb.append("Circular dependency found!");
        sb.append(String.format("\n%s<----%s", arrowDown, arrowUp));
        sb.append(String.format("\n%s     %s %s", arrowDown, arrowUp, service.getServiceType()));

        for (ServiceDetails trace : serviceTrace) {
            if (service.equals(trace)) {
                break;
            }

            sb.append(String.format("\n%s     %s %s", arrowDown, arrowUp, trace.getServiceType()));
        }

        sb.append(String.format("\n%s---->%s", arrowDown, arrowUp));

        throw new CircularDependencyException(sb.toString());
    }
}
