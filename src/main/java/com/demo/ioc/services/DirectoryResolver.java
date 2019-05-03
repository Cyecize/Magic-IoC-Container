package com.demo.ioc.services;

import com.demo.ioc.models.Directory;

public interface DirectoryResolver {

    Directory resolveDirectory(Class<?> startupClass);
}
