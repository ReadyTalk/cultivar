package com.readytalk.cultivar.discovery;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.AbstractIdleService;
import com.readytalk.cultivar.internal.Private;

/**
 * Manages the lifecycle of a single ServiceDiscovery instance.
 */
@Beta
@ThreadSafe
public class ServiceDiscoveryManager<T> extends AbstractIdleService implements DiscoveryService {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceDiscoveryManager.class);

    private final ServiceDiscovery<T> discovery;

    @Inject
    ServiceDiscoveryManager(@Private final ServiceDiscovery<T> discovery) {
        this.discovery = discovery;
    }

    @Override
    protected void startUp() throws Exception {
        LOG.debug("Starting ServiceDiscoveryManager for ServiceDiscovery: {}", discovery);

        discovery.start();
    }

    @Override
    protected void shutDown() throws IOException {
        LOG.debug("Shutting down ServiceDiscoveryManager for ServiceDiscovery: {}", discovery);

        discovery.close();
    }
}
