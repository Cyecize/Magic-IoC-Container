package com.cyecize.ioc.config;

import com.cyecize.ioc.config.configurations.GeneralConfiguration;
import com.cyecize.ioc.config.configurations.InstantiationConfiguration;
import com.cyecize.ioc.config.configurations.ScanningConfiguration;

public class MagicConfiguration {

    private final ScanningConfiguration annotations;

    private final InstantiationConfiguration instantiations;

    private final GeneralConfiguration generalConfiguration;

    public MagicConfiguration() {
        this.annotations = new ScanningConfiguration(this);
        this.instantiations = new InstantiationConfiguration(this);
        this.generalConfiguration = new GeneralConfiguration(this);
    }

    public ScanningConfiguration scanning() {
        return this.annotations;
    }

    public InstantiationConfiguration instantiations() {
        return this.instantiations;
    }

    public GeneralConfiguration general() {
        return this.generalConfiguration;
    }

    public MagicConfiguration build() {
        return this;
    }
}
