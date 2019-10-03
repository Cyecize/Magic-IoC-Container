package com.cyecize.ioc.config;

import com.cyecize.ioc.config.configurations.InstantiationConfiguration;
import com.cyecize.ioc.config.configurations.ScanningConfiguration;

public class MagicConfiguration {

    private final ScanningConfiguration annotations;

    private final InstantiationConfiguration instantiations;

    public MagicConfiguration() {
        this.annotations = new ScanningConfiguration(this);
        this.instantiations = new InstantiationConfiguration(this);
    }

    public ScanningConfiguration annotations() {
        return this.annotations;
    }

    public InstantiationConfiguration instantiations() {
        return this.instantiations;
    }

    public MagicConfiguration build() {
        return this;
    }
}
