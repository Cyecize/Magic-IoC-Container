package com.demo.ioc.services;

import com.demo.ioc.exceptions.ClassLocationException;

import java.util.Set;

/**
 * Service for locating classes in the application context.
 */
public interface ClassLocator {

    Set<Class<?>> locateClasses(String directory) throws ClassLocationException;
}
