package com.demo.ioc.services;

import com.demo.ioc.annotations.*;
import com.demo.ioc.config.configurations.CustomAnnotationsConfiguration;
import com.demo.ioc.models.ServiceDetails;
import com.demo.ioc.utils.ServiceDetailsConstructComparator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link ServicesScanningService} implementation.
 * Iterates all located classes and looks for classes with @Service
 * annotation or one provided by the client and then collects data for that class.
 */
public class ServicesScanningServiceImpl implements ServicesScanningService {

    /**
     * Configuration containing annotations provided by the client.
     */
    private final CustomAnnotationsConfiguration configuration;

    public ServicesScanningServiceImpl(CustomAnnotationsConfiguration configuration) {
        this.configuration = configuration;
        this.init();
    }

    /**
     * Iterates all given classes and filters those that have {@link Service} annotation
     * or one prided by the client and collects details for those classes.
     *
     * @param locatedClasses given set of classes.
     * @return set or services and their collected details.
     */
    @Override
    public Set<ServiceDetails> mapServices(Set<Class<?>> locatedClasses) {
        final Set<ServiceDetails> serviceDetailsStorage = new HashSet<>();
        final Set<Class<? extends Annotation>> customServiceAnnotations = configuration.getCustomServiceAnnotations();

        for (Class<?> cls : locatedClasses) {
            if (cls.isInterface()) {
                continue;
            }

            for (Annotation annotation : cls.getAnnotations()) {
                if (customServiceAnnotations.contains(annotation.annotationType())) {
                    ServiceDetails serviceDetails = new ServiceDetails(
                            cls,
                            annotation,
                            this.findSuitableConstructor(cls),
                            this.findVoidMethodWithZeroParamsAndAnnotations(PostConstruct.class, cls),
                            this.findVoidMethodWithZeroParamsAndAnnotations(PreDestroy.class, cls),
                            this.findBeans(cls)
                    );

                    serviceDetailsStorage.add(serviceDetails);

                    break;
                }
            }
        }

        return serviceDetailsStorage.stream()
                .sorted(new ServiceDetailsConstructComparator())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Looks for a constructor from the given class that has {@link Autowired} annotation
     * or gets the first one.
     *
     * @param cls - the given class.
     * @return suitable constructor.
     */
    private Constructor<?> findSuitableConstructor(Class<?> cls) {
        for (Constructor<?> ctr : cls.getDeclaredConstructors()) {
            if (ctr.isAnnotationPresent(Autowired.class)) {
                ctr.setAccessible(true);
                return ctr;
            }
        }

        return cls.getConstructors()[0];
    }

    private Method findVoidMethodWithZeroParamsAndAnnotations(Class<? extends Annotation> annotation, Class<?> cls) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getParameterCount() != 0 ||
                    (method.getReturnType() != void.class && method.getReturnType() != Void.class) ||
                    !method.isAnnotationPresent(annotation)) {
                continue;
            }

            method.setAccessible(true);
            return method;
        }

        return null;
    }

    /**
     * Scans a given class for methods that are considered beans.
     *
     * @param cls the given class.
     * @return array or method references that are bean compliant.
     */
    private Method[] findBeans(Class<?> cls) {
        final Set<Class<? extends Annotation>> beanAnnotations = this.configuration.getCustomBeanAnnotations();
        final Set<Method> beanMethods = new HashSet<>();

        for (Method method : cls.getDeclaredMethods()) {
            if (method.getParameterCount() != 0 || method.getReturnType() == void.class || method.getReturnType() == Void.class) {
                continue;
            }

            for (Class<? extends Annotation> beanAnnotation : beanAnnotations) {
                if (method.isAnnotationPresent(beanAnnotation)) {
                    method.setAccessible(true);
                    beanMethods.add(method);

                    break;
                }
            }
        }

        return beanMethods.toArray(Method[]::new);
    }

    /**
     * Adds the platform's default annotations for services and beans on top of the
     * ones that the client might have provided.
     */
    private void init() {
        this.configuration.getCustomBeanAnnotations().add(Bean.class);
        this.configuration.getCustomServiceAnnotations().add(Service.class);
    }
}
