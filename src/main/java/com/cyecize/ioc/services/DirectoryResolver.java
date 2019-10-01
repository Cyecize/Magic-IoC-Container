package com.cyecize.ioc.services;

import com.cyecize.ioc.models.Directory;

public interface DirectoryResolver {

    Directory resolveDirectory(Class<?> startupClass);
}
