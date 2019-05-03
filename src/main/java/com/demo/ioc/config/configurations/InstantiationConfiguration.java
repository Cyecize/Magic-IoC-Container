package com.demo.ioc.config.configurations;

import com.demo.ioc.config.BaseSubConfiguration;
import com.demo.ioc.config.MagicConfiguration;
import com.demo.ioc.constants.Constants;

public class InstantiationConfiguration extends BaseSubConfiguration {

    private int maximumAllowedIterations;

    public InstantiationConfiguration(MagicConfiguration parentConfig) {
        super(parentConfig);
        this.maximumAllowedIterations = Constants.MAX_NUMBER_OF_INSTANTIATION_ITERATIONS;
    }

    public InstantiationConfiguration setMaximumNumberOfAllowedIterations(int num) {
        this.maximumAllowedIterations = num;
        return this;
    }

    public int getMaximumAllowedIterations() {
        return this.maximumAllowedIterations;
    }
}
