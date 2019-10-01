package com.cyecize.ioc.config;

import com.cyecize.ioc.config.configurations.InstantiationConfiguration;
import com.cyecize.ioc.config.configurations.CustomAnnotationsConfiguration;

public class MagicConfiguration {

    private final CustomAnnotationsConfiguration annotations;

    private final InstantiationConfiguration instantiations;

    public MagicConfiguration() {
        this.annotations = new CustomAnnotationsConfiguration(this);
        this.instantiations = new InstantiationConfiguration(this);
    }

    public CustomAnnotationsConfiguration annotations() {
        return this.annotations;
    }

    public InstantiationConfiguration instantiations() {
        return this.instantiations;
    }

    public MagicConfiguration build() {
        return this;
    }
}
