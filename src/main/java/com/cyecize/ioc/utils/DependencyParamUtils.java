package com.cyecize.ioc.utils;

import com.cyecize.ioc.models.DependencyParam;
import com.cyecize.ioc.models.ResolvedServiceDto;
import com.cyecize.ioc.models.ServiceBeanDetails;
import com.cyecize.ioc.models.ServiceDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DependencyParamUtils {
    public static boolean isServiceCompatible(ServiceDetails serviceDetails, Class<?> requiredType, String instanceName) {
        //service type is assignable from (concrete instance or proxy)
        // and (instanceName if not null equals service's instance name)

        final boolean isRequiredTypeAssignable = requiredType.isAssignableFrom(serviceDetails.getServiceType());
        final boolean isRequiredTypeAssignable2 = serviceDetails.getInstance() != null &&
                requiredType.isAssignableFrom(serviceDetails.getInstance().getClass());

        final boolean instanceNameMatches = instanceName == null ||
                instanceName.equalsIgnoreCase(serviceDetails.getInstanceName());

        return (isRequiredTypeAssignable || isRequiredTypeAssignable2) && instanceNameMatches;
    }

    public static List<ResolvedServiceDto> findAllCompatibleServices(DependencyParam dependencyParam,
                                                                     Collection<ServiceDetails> allServiceDetails) {
        final List<ResolvedServiceDto> resolvedServices = new ArrayList<>();

        for (ServiceDetails service : allServiceDetails) {
            if (dependencyParam.isCompatible(service)) {
                resolvedServices.add(new ResolvedServiceDto(service, service));
            }

            for (ServiceBeanDetails bean : service.getBeans()) {
                if (dependencyParam.isCompatible(bean)) {
                    resolvedServices.add(new ResolvedServiceDto(service, bean));
                }
            }
        }

        return resolvedServices;
    }

    public static ResolvedServiceDto getNamedInstanceService(Class<?> cls,
                                                             String nameOfInstance,
                                                             Collection<ServiceDetails> serviceDetails) {
        for (ServiceDetails service : serviceDetails) {
            if (nameOfInstance.equalsIgnoreCase(service.getInstanceName()) &&
                    cls.isAssignableFrom(service.getServiceType())) {
                return new ResolvedServiceDto(service, service);
            }

            for (ServiceBeanDetails bean : service.getBeans()) {
                if (nameOfInstance.equalsIgnoreCase(bean.getInstanceName()) &&
                        cls.isAssignableFrom(bean.getServiceType())) {
                    return new ResolvedServiceDto(service, bean);
                }
            }
        }

        return null;
    }
}
