package com.cyecize.ioc.config.configurations;

import com.cyecize.ioc.constants.Constants;
import com.cyecize.ioc.config.BaseSubConfiguration;
import com.cyecize.ioc.config.MagicConfiguration;

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
