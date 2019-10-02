package com.cyecize.ioc.services;

import com.cyecize.ioc.annotations.*;
import com.cyecize.ioc.models.ServiceDetails;
import com.cyecize.ioc.config.configurations.CustomAnnotationsConfiguration;
import com.cyecize.ioc.utils.ServiceDetailsConstructComparator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
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
     * Iterates scanned classes with @{@link Service} or user specified annotation
     * and creates a {@link ServiceDetails} object with the collected information.
     *
     * @param locatedClasses given set of classes.
     * @return set or services and their collected details.
     */
    @Override
    public Set<ServiceDetails> mapServices(Set<Class<?>> locatedClasses) {
        final Map<Class<?>, List<Class<? extends Annotation>>> onlyServiceClasses = this.filterServiceClasses(locatedClasses);

        final Set<ServiceDetails> serviceDetailsStorage = new HashSet<>();

        for (Map.Entry<Class<?>, List<Class<? extends Annotation>>> serviceAnnotationEntry : onlyServiceClasses.entrySet()) {
            final Class<?> cls = serviceAnnotationEntry.getKey();
            final List<Class<? extends Annotation>> annotations = serviceAnnotationEntry.getValue();

            final ServiceDetails serviceDetails = new ServiceDetails(
                    cls,
                    annotations,
                    this.findSuitableConstructor(cls),
                    this.findVoidMethodWithZeroParamsAndAnnotations(PostConstruct.class, cls),
                    this.findVoidMethodWithZeroParamsAndAnnotations(PreDestroy.class, cls),
                    this.findBeans(cls)
            );

            serviceDetailsStorage.add(serviceDetails);

        }

        return serviceDetailsStorage.stream()
                .sorted(new ServiceDetailsConstructComparator())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Iterates all given classes and filters those that have {@link Service} annotation
     * or one prided by the client.
     *
     * @return service annotated classes.
     */
    private Map<Class<?>, List<Class<? extends Annotation>>> filterServiceClasses(Collection<Class<?>> scannedClasses) {
        final Set<Class<? extends Annotation>> serviceAnnotations = this.configuration.getCustomServiceAnnotations();
        final Map<Class<?>, List<Class<? extends Annotation>>> locatedClasses = new HashMap<>();

        for (Class<?> cls : scannedClasses) {
            if (cls.isInterface() || cls.isEnum() || cls.isAnnotation()) {
                continue;
            }

            for (Annotation annotation : cls.getAnnotations()) {
                if (serviceAnnotations.contains(annotation.annotationType())) {
                    locatedClasses.put(cls, Collections.singletonList(annotation.annotationType()));
                    break;
                }

                if (annotation.annotationType().isAnnotationPresent(AliasFor.class)) {
                    final Class<? extends Annotation> aliasValue = annotation.annotationType().getAnnotation(AliasFor.class).value();

                    if (serviceAnnotations.contains(aliasValue)) {
                        locatedClasses.put(cls, List.of(aliasValue, annotation.annotationType()));
                        break;
                    }
                }
            }
        }

        return locatedClasses;
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

            for (Annotation declaredAnnotation : ctr.getDeclaredAnnotations()) {
                if (declaredAnnotation.annotationType().isAnnotationPresent(AliasFor.class)) {
                    final Class<? extends Annotation> aliasValue = declaredAnnotation.annotationType().getAnnotation(AliasFor.class).value();
                    if (aliasValue == Autowired.class) {
                        ctr.setAccessible(true);
                        return ctr;
                    }
                }
            }
        }

        return cls.getConstructors()[0];
    }

    private Method findVoidMethodWithZeroParamsAndAnnotations(Class<? extends Annotation> annotation, Class<?> cls) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getParameterCount() != 0 ||
                    (method.getReturnType() != void.class && method.getReturnType() != Void.class)) {
                continue;
            }

            if (method.isAnnotationPresent(annotation)) {
                method.setAccessible(true);
                return method;
            }

            for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
                if (declaredAnnotation.annotationType().isAnnotationPresent(AliasFor.class)) {
                    final Class<? extends Annotation> aliasValue = declaredAnnotation.annotationType().getAnnotation(AliasFor.class).value();
                    if (aliasValue == annotation) {
                        method.setAccessible(true);
                        return method;
                    }
                }
            }
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

                for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
                    if (declaredAnnotation.annotationType().isAnnotationPresent(AliasFor.class)) {
                        final Class<? extends Annotation> aliasValue = declaredAnnotation.annotationType().getAnnotation(AliasFor.class).value();

                        if (aliasValue == beanAnnotation) {
                            method.setAccessible(true);
                            beanMethods.add(method);

                            break;
                        }
                    }
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
