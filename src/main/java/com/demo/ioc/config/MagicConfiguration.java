package com.demo.ioc.config;

import com.demo.ioc.config.configurations.CustomAnnotationsConfiguration;
import com.demo.ioc.config.configurations.InstantiationConfiguration;

public class MagicConfiguration {

    private final CustomAnnotationsConfiguration annotations;

    private final InstantiationConfiguration instantitations;

    public MagicConfiguration() {
        this.annotations = new CustomAnnotationsConfiguration(this);
        this.instantitations = new InstantiationConfiguration(this);
    }

    public CustomAnnotationsConfiguration annotations() {
        return this.annotations;
    }

    public InstantiationConfiguration instantiations() {
        return this.instantitations;
    }

    public MagicConfiguration build() {
        return this;
    }
}
