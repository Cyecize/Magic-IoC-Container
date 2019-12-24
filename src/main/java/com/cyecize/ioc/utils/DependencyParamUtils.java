package com.cyecize.ioc.utils;

import com.cyecize.ioc.models.DependencyParam;
import com.cyecize.ioc.models.ServiceDetails;

import java.util.Collection;

public final class DependencyParamUtils {

    public static boolean dependencyParamsResolved(Collection<DependencyParam> dependencyParams) {
        for (DependencyParam dependencyParam : dependencyParams) {
            if (dependencyParam.getInstance() == null && dependencyParam.isValuePresent()) {
                return false;
            }
        }

        return true;
    }

    public static boolean isDependencyRequired(Collection<DependencyParam> dependencyParams, ServiceDetails serviceDetails) {
        for (DependencyParam dependencyParam : dependencyParams) {
           if (isDependencyRequired(dependencyParam, serviceDetails)) {
               return true;
           }
        }

        return false;
    }

    public static boolean isDependencyRequired(DependencyParam dependencyParam, ServiceDetails serviceDetails) {
        if (dependencyParam.getInstance() != null) {
            return false;
        }

        return ServiceCompatibilityUtils.isServiceCompatible(
                serviceDetails,
                dependencyParam.getDependencyType(),
                dependencyParam.getInstanceName());
    }
}
