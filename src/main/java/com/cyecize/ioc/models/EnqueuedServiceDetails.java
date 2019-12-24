package com.cyecize.ioc.models;

import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.annotations.Qualifier;
import com.cyecize.ioc.utils.AliasFinder;
import com.cyecize.ioc.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.LinkedList;

/**
 * Simple POJO class that keeps information about a service, its
 * required dependencies and the ones that are already resolved.
 */
public class EnqueuedServiceDetails {

    /**
     * Reference to the target service.
     */
    private final ServiceDetails serviceDetails;

    /**
     * List of dependencies that the target constructor of the service requires.
     */
    private final LinkedList<DependencyParam> constructorParams;

    /**
     * List of dependencies that are required from {@link Autowired} annotated fields.
     */
    private final LinkedList<DependencyParam> fieldDependencies;

    public EnqueuedServiceDetails(ServiceDetails serviceDetails) {
        this.serviceDetails = serviceDetails;
        this.constructorParams = new LinkedList<>();
        this.fieldDependencies = new LinkedList<>();
        this.fillConstructorParams();
        this.fillFieldDependencyTypes();
    }

    public ServiceDetails getServiceDetails() {
        return this.serviceDetails;
    }

    public LinkedList<DependencyParam> getConstructorParams() {
        return this.constructorParams;
    }

    public Object[] getConstructorInstances() {
        return this.constructorParams.stream()
                .map(DependencyParam::getInstance)
                .toArray(Object[]::new);
    }

    public LinkedList<DependencyParam> getFieldDependencies() {
        return this.fieldDependencies;
    }

    public Object[] getFieldInstances() {
        return this.fieldDependencies.stream()
                .map(DependencyParam::getInstance)
                .toArray(Object[]::new);
    }

    private void fillConstructorParams() {
        for (Parameter parameter : this.serviceDetails.getTargetConstructor().getParameters()) {
            this.constructorParams.add(new DependencyParam(
                    parameter.getType(),
                    this.getInstanceName(parameter.getDeclaredAnnotations()),
                    parameter.getDeclaredAnnotations()
            ));
        }
    }

    private void fillFieldDependencyTypes() {
        for (Field autowireAnnotatedField : this.serviceDetails.getAutowireAnnotatedFields()) {
            this.fieldDependencies.add(new DependencyParam(
                    autowireAnnotatedField.getType(),
                    this.getInstanceName(autowireAnnotatedField.getDeclaredAnnotations()),
                    autowireAnnotatedField.getDeclaredAnnotations()
            ));
        }
    }

    private String getInstanceName(Annotation[] annotations) {
        final Annotation annotation = AliasFinder.getAnnotation(annotations, Qualifier.class);

        if (annotation != null) {
            return AnnotationUtils.getAnnotationValue(annotation).toString();
        }

        return null;
    }

    @Override
    public String toString() {
        return this.serviceDetails.getServiceType().getName();
    }
}
