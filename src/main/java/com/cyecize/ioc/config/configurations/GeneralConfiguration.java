package com.cyecize.ioc.config.configurations;

import com.cyecize.ioc.config.BaseSubConfiguration;
import com.cyecize.ioc.config.MagicConfiguration;

public class GeneralConfiguration extends BaseSubConfiguration {

    private boolean runInNewThread;

    public GeneralConfiguration(MagicConfiguration parentConfig) {
        super(parentConfig);
    }

    public GeneralConfiguration runInNewThread(boolean runInNewThread) {
        this.runInNewThread = runInNewThread;
        return this;
    }

    public boolean isRunInNewThread() {
        return this.runInNewThread;
    }
}
