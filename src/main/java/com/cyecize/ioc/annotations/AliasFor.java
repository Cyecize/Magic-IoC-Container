package com.cyecize.ioc.annotations;

import com.cyecize.ioc.config.configurations.ScanningConfiguration;

import java.lang.annotation.*;

/**
 * You can use this annotation on your own annotations if you want to make them compatible with MagicInjector.
 * This annotation can be used to create aliases for {@link Autowired}, {@link PostConstruct}, {@link PreDestroy}.
 *
 * For services and beans you might want to use {@link ScanningConfiguration}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface AliasFor {
    Class<? extends Annotation> value();
}
