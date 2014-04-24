package org.waterprinciple.cultivar.discovery;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import com.google.common.annotations.Beta;
import org.apache.curator.x.discovery.ServiceProvider;
import org.waterprinciple.cultivar.internal.Private;

import com.google.common.util.concurrent.AbstractIdleService;

/**
 * A lifecycle manager around a single ServiceProvider instance.
 */
@Beta
@ThreadSafe
public class ServiceProviderManager<T> extends AbstractIdleService implements DiscoveryService {

    private final ServiceProvider<T> provider;

    @Inject
    ServiceProviderManager(@Private final ServiceProvider<T> provider) {
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
