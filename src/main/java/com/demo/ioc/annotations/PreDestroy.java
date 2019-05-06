package com.demo.ioc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to map method within a service that will be executed right before
 * a service instance is being destroyed.
 * <p>
 * It order for it to work, it should be placed on void methods with zero params.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreDestroy {

}
