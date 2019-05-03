package com.demo.ioc;

import com.demo.ioc.annotations.StartUp;
import com.demo.ioc.config.MagicConfiguration;
import com.demo.ioc.enums.DirectoryType;
import com.demo.ioc.models.Directory;
import com.demo.ioc.models.ServiceDetails;
import com.demo.ioc.services.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class MagicInjector {

    public static final DependencyContainer dependencyContainer;

    static {
        dependencyContainer = new DependencyContainerImpl();
    }

    public static void run(Class<?> startupClass) {
        run(startupClass, new MagicConfiguration());
    }

    public static void run(Class<?> startupClass, MagicConfiguration configuration) {
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

        Set<ServiceDetails<?>> mappedServices = scanningService.mapServices(locatedClasses);
        List<ServiceDetails<?>> serviceDetails = instantiationService.instantiateServicesAndBeans(mappedServices);

        dependencyContainer.init(serviceDetails, objectInstantiationService);
        runStartUpMethod(startupClass);
    }

    private static void runStartUpMethod(Class<?> startupClass) {
        ServiceDetails<?> serviceDetails = dependencyContainer.getServiceDetails(startupClass);

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
