package com.cyecize.ioc;

import com.cyecize.ioc.annotations.StartUp;
import com.cyecize.ioc.models.ServiceDetails;
import com.cyecize.ioc.services.*;
import com.cyecize.ioc.config.MagicConfiguration;
import com.cyecize.ioc.enums.DirectoryType;
import com.cyecize.ioc.models.Directory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * Application starting point.
 * <p>
 * Contains multiple starting point methods.
 * Holds an instance of Dependency Container.
 */
public class MagicInjector {

    /**
     * Stores all loaded classes.
     * There is only one instance of a dependency container.
     */
    public static final DependencyContainer dependencyContainer;

    static {
        dependencyContainer = new DependencyContainerImpl();
    }

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
        ServicesScanningService scanningService = new ServicesScanningServiceImpl(configuration.annotations());
        ObjectInstantiationService objectInstantiationService = new ObjectInstantiationServiceImpl();
        ServicesInstantiationService instantiationService = new ServicesInstantiationServiceImpl(
                configuration.instantiations(),
                objectInstantiationService
        );

        Directory directory = new DirectoryResolverImpl().resolveDirectory(startupClass);

        ClassLocator classLocator = new ClassLocatorForDirectory();
        if (directory.getDirectoryType() == DirectoryType.JAR_FILE) {
            classLocator = new ClassLocatorForJarFile();
        }

        Set<Class<?>> locatedClasses = classLocator.locateClasses(directory.getDirectory());

        Set<ServiceDetails> mappedServices = scanningService.mapServices(locatedClasses);
        List<ServiceDetails> serviceDetails = instantiationService.instantiateServicesAndBeans(mappedServices);

        dependencyContainer.init(serviceDetails, objectInstantiationService);
        runStartUpMethod(startupClass);

        return dependencyContainer;
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
    private static void runStartUpMethod(Class<?> startupClass) {
        ServiceDetails serviceDetails = dependencyContainer.getServiceDetails(startupClass);
		
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
                declaredMethod.invoke(serviceDetails.getInstance());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            return;
        }
    }
}
