package com.cyecize.ioc.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface AliasFor {
    Class<? extends Annotation> value();
}
