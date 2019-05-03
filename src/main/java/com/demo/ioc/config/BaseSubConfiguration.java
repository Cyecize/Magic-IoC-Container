package com.demo.ioc.config;

public abstract class BaseSubConfiguration {

    private final MagicConfiguration parentConfig;

    protected BaseSubConfiguration(MagicConfiguration parentConfig) {
        this.parentConfig = parentConfig;
    }

    public MagicConfiguration and() {
        return this.parentConfig;
    }
}
