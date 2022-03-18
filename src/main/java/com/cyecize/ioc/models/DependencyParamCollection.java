package com.cyecize.ioc.models;

import com.cyecize.ioc.utils.CollectionUtils;
import com.cyecize.ioc.utils.GenericsUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DependencyParamCollection extends DependencyParam {

    private final List<Object> instances = new ArrayList<>();

    /**
     * Stores classes whom instances have not been added yet.
     */
    private List<Class<?>> leftOverClasses;

    private final Class<?> collectionType;

    public DependencyParamCollection(ParameterizedType parameterizedType,
                                     Class<?> dependencyType,
                                     String instanceName,
                                     Annotation[] annotations) {
        super(GenericsUtils.getRawType(parameterizedType), instanceName, annotations);
        this.collectionType = dependencyType;
    }

    @Override
    public void setAllAvailableCompatibleClasses(List<Class<?>> allAvailableCompatibleClasses) {
        super.setAllAvailableCompatibleClasses(allAvailableCompatibleClasses);
        this.leftOverClasses = new ArrayList<>(allAvailableCompatibleClasses);
    }

    @Override
    public boolean isUnresolved() {
        return super.isValuePresent()
                && this.getAllAvailableCompatibleClasses().size() > 0
                && this.getAllAvailableCompatibleClasses().size() != this.instances.size();
    }

    @Override
    public boolean isCompatible(ServiceDetails serviceDetails) {
        if (!super.isCompatible(serviceDetails)) {
            return false;
        }

        final Class<?> instanceType = serviceDetails.getInstance().getClass();
        return this.leftOverClasses.stream().anyMatch(cls -> cls.isAssignableFrom(instanceType));
    }

    @Override
    public Object getInstance() {
        if (this.instances.size() == 0) {
            return null;
        }

        final Collection<Object> collection = CollectionUtils.createInstanceOfCollection(this.collectionType);
        collection.addAll(this.instances);

        return collection;
    }

    @Override
    public void setInstance(Object instance) {
        final Class<?> instanceType = this.leftOverClasses.stream()
                .filter(cls -> cls.isAssignableFrom(instance.getClass()))
                .findFirst().orElse(null);

        if (instanceType == null) {
            return;
        }

        this.leftOverClasses.remove(instanceType);
        this.instances.add(instance);
    }
}
