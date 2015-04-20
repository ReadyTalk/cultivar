package com.readytalk.cultivar.discovery;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import com.google.common.annotations.Beta;
import com.readytalk.cultivar.internal.Private;
import org.apache.curator.x.discovery.ServiceProvider;

import com.google.common.util.concurrent.AbstractIdleService;

/**
 * A lifecycle manager around a single ServiceProvider instance.
 */
@Beta
@ThreadSafe
public class ServiceProviderManager<T> extends AbstractIdleService implements DiscoveryService {

    private final ServiceProvider<T> provider;

    @Inject
    protected ServiceProviderManager(@Private final ServiceProvider<T> provider) {
        this.provider = provider;

    }

    @Override
    protected void startUp() throws Exception {
        provider.start();

    }

    @Override
    protected void shutDown() throws IOException {
        provider.close();
    }
}
