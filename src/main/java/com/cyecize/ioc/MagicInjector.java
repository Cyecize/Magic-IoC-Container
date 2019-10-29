package com.cyecize.ioc;

import com.cyecize.ioc.annotations.StartUp;
import com.cyecize.ioc.models.ServiceDetails;
import com.cyecize.ioc.services.*;
import com.cyecize.ioc.config.MagicConfiguration;
import com.cyecize.ioc.enums.DirectoryType;
import com.cyecize.ioc.models.Directory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Application starting point.
 * <p>
 * Contains multiple starting point methods.
 */
public class MagicInjector {

    /**
     * Overload with default configuration.
     *
     * @param startupClass any class from the client side.
     */
    public static DependencyContainer run(Class<?> startupClass) {
        return run(startupClass, new MagicConfiguration());
    }

    /**
     * Runs with startup class.
     *
     * @param startupClass  any class from the client side.
     * @param configuration client configuration.
     */
    public static DependencyContainer run(Class<?> startupClass, MagicConfiguration configuration) {
        final DependencyContainer dependencyContainer = run(new File[]{
                new File(new DirectoryResolverImpl().resolveDirectory(startupClass).getDirectory()),
        }, configuration);

        runStartUpMethod(startupClass, dependencyContainer);

        return dependencyContainer;
    }

    public static DependencyContainer run(File[] startupDirectories, MagicConfiguration configuration) {
        final ServicesScanningService scanningService = new ServicesScanningServiceImpl(configuration.scanning());
        final ObjectInstantiationService objectInstantiationService = new ObjectInstantiationServiceImpl();
        final ServicesInstantiationService instantiationService = new ServicesInstantiationServiceImpl(
                configuration.instantiations(),
                objectInstantiationService
        );

        final Set<Class<?>> locatedClasses = new HashSet<>();
        final Set<ServiceDetails> mappedServices = new HashSet<>();
        final List<ServiceDetails> serviceDetails = new ArrayList<>();

        final Thread runner = new Thread(() -> {
            locatedClasses.addAll(locateClasses(startupDirectories));
            mappedServices.addAll(scanningService.mapServices(locatedClasses));
            serviceDetails.addAll(instantiationService.instantiateServicesAndBeans(mappedServices));
        });

        runner.setContextClassLoader(configuration.scanning().getClassLoader());
        runner.start();
        try {
            runner.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final DependencyContainer dependencyContainer = new DependencyContainerImpl();
        dependencyContainer.init(locatedClasses, serviceDetails, objectInstantiationService);

        return dependencyContainer;
    }

    private static Set<Class<?>> locateClasses(File[] startupDirectories) {
        final Set<Class<?>> locatedClasses = new HashSet<>();
        final DirectoryResolver directoryResolver = new DirectoryResolverImpl();

        for (File startupDirectory : startupDirectories) {
            final Directory directory = directoryResolver.resolveDirectory(startupDirectory);

            ClassLocator classLocator = new ClassLocatorForDirectory();
            if (directory.getDirectoryType() == DirectoryType.JAR_FILE) {
                classLocator = new ClassLocatorForJarFile();
            }

            locatedClasses.addAll(classLocator.locateClasses(directory.getDirectory()));
        }

        return locatedClasses;
    }

    /**
     * This method calls executes when all services are loaded.
     * <p>
     * Looks for instantiated service from the given type.
     * <p>
     * If instance is found, looks for void method with 0 params
     * and with with @StartUp annotation and executes it.
     *
     * @param startupClass any class from the client side.
     */
    private static void runStartUpMethod(Class<?> startupClass, DependencyContainer dependencyContainer) {
        final ServiceDetails serviceDetails = dependencyContainer.getServiceDetails(startupClass);

        if (serviceDetails == null) {
            return;
        }

        for (Method declaredMethod : serviceDetails.getServiceType().getDeclaredMethods()) {
            if (declaredMethod.getParameterCount() != 0 ||
                    (declaredMethod.getReturnType() != void.class &&
                            declaredMethod.getReturnType() != Void.class)
                    || !declaredMethod.isAnnotationPresent(StartUp.class)) {
                continue;
            }

            declaredMethod.setAccessible(true);
            try {
                declaredMethod.invoke(serviceDetails.getActualInstance());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            return;
        }
    }
}
