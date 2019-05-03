package com.demo.ioc.services;

import com.demo.ioc.exceptions.ClassLocationException;

import java.util.Set;

public interface ClassLocator {

    Set<Class<?>> locateClasses(String directory) throws ClassLocationException;
}
