package com.readytalk.cultivar.discovery.test;

import com.google.inject.Inject;
import com.readytalk.cultivar.discovery.ServiceProviderManager;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings("ConstantConditions")
@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
public class ControllableServiceProviderManager<T> extends ServiceProviderManager<T> {
    @Inject
    ControllableServiceProviderManager() {
        super(null);
    }

    @Override
    protected void startUp() throws Exception {

    }

    @Override
    protected void shutDown() {

    }

}
