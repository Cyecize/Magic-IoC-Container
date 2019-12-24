package com.cyecize.ioc.utils;

import com.cyecize.ioc.models.ServiceDetails;

public final class ServiceCompatibilityUtils {

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
}
