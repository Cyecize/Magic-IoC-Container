package com.cyecize.ioc.handlers;

import com.cyecize.ioc.models.DependencyParam;

public interface DependencyResolver {

    boolean canResolve(DependencyParam dependencyParam);

    Object resolve(DependencyParam dependencyParam);
}
