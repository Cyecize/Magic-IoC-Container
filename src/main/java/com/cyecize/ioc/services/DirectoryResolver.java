package com.cyecize.ioc.services;

import com.cyecize.ioc.models.Directory;

import java.io.File;

public interface DirectoryResolver {

    Directory resolveDirectory(Class<?> startupClass);

    Directory resolveDirectory(File directory);
}
