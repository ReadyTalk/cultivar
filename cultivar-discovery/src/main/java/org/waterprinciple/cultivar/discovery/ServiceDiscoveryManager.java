package org.waterprinciple.cultivar.discovery;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.waterprinciple.cultivar.internal.Private;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.AbstractIdleService;

/**
 * Manages the lifecycle of a single ServiceDiscovery instance.
 */
@Beta
@ThreadSafe
public class ServiceDiscoveryManager<T> extends AbstractIdleService implements DiscoveryService {

    private final ServiceDiscovery<T> discovery;

    @Inject
    ServiceDiscoveryManager(@Private final ServiceDiscovery<T> discovery) {
        this.discovery = discovery;
    }

    @Override
    protected void startUp() throws Exception {
        discovery.start();

    }

    @Override
    protected void shutDown() throws IOException {
        discovery.close();
    }
}
