package com.cyecize.ioc.models;

import com.cyecize.ioc.utils.CollectionUtils;
import com.cyecize.ioc.utils.GenericsUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DependencyParamCollection extends DependencyParam {
    private final Class<?> collectionType;

    private List<ServiceDetails> serviceDetails;

    public DependencyParamCollection(ParameterizedType parameterizedType,
                                     Class<?> dependencyType,
                                     String instanceName,
                                     Annotation[] annotations) {
        super(GenericsUtils.getRawType(parameterizedType), instanceName, annotations);
        this.collectionType = dependencyType;
    }

    public void setServiceDetails(List<ServiceDetails> serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    @Override
    public Object getInstance() {
        if (super.getDependencyResolver() != null) {
            return super.getInstance();
        }

        final Collection<Object> collection = CollectionUtils.createInstanceOfCollection(this.collectionType);
        collection.addAll(this.serviceDetails.stream().map(ServiceDetails::getInstance).collect(Collectors.toList()));

        return collection;
    }
}
