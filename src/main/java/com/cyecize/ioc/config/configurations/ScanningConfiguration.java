package com.cyecize.ioc.config.configurations;

import com.cyecize.ioc.config.BaseSubConfiguration;
import com.cyecize.ioc.config.MagicConfiguration;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ScanningConfiguration extends BaseSubConfiguration {

    private final Set<Class<? extends Annotation>> customServiceAnnotations;

    private final Set<Class<? extends Annotation>> customBeanAnnotations;

    private final Set<Class<?>> additionalClasses;

    public ScanningConfiguration(MagicConfiguration parentConfig) {
        super(parentConfig);
        this.customServiceAnnotations = new HashSet<>();
        this.customBeanAnnotations = new HashSet<>();
        this.additionalClasses = new HashSet<>();
    }

    public ScanningConfiguration addCustomServiceAnnotation(Class<? extends Annotation> annotation) {
        this.customServiceAnnotations.add(annotation);
        return this;
    }

    public ScanningConfiguration addCustomServiceAnnotations(Collection<Class<? extends Annotation>> annotations) {
        this.customServiceAnnotations.addAll(Set.copyOf(annotations));
        return this;
    }

    public ScanningConfiguration addCustomBeanAnnotation(Class<? extends Annotation> annotation) {
        this.customBeanAnnotations.add(annotation);
        return this;
    }

    public ScanningConfiguration addCustomBeanAnnotations(Collection<Class<? extends Annotation>> annotations) {
        this.customBeanAnnotations.addAll(Set.copyOf(annotations));
        return this;
    }

    public ScanningConfiguration addAdditionalClassesForScanning(Class<?>... classes) {
        this.additionalClasses.addAll(Arrays.asList(classes));
        return this;
    }

    public Set<Class<? extends Annotation>> getCustomBeanAnnotations() {
        return this.customBeanAnnotations;
    }

    public Set<Class<? extends Annotation>> getCustomServiceAnnotations() {
        return this.customServiceAnnotations;
    }

    public Set<Class<?>> getAdditionalClasses() {
        return this.additionalClasses;
    }
}
