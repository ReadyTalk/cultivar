package com.readytalk.cultivar.discovery;

import static com.google.common.base.Preconditions.checkState;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.GuardedBy;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Atomics;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.readytalk.cultivar.internal.Private;

/**
 * A simple registration service that registers and unregisters an instance coming from a provider.
 * 
 * @param <T>
 *            The payload type for discovery.
 */
class DefaultRegistrationService<T> extends AbstractIdleService implements RegistrationService<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRegistrationService.class);

    private final ServiceDiscovery<T> discovery;
    private final Provider<ServiceInstance<T>> provider;

    @GuardedBy("this")
    private final AtomicReference<ServiceInstance<T>> instance = Atomics.newReference();

    @Inject
    DefaultRegistrationService(@Private final ServiceDiscovery<T> discovery,
            @Private final Provider<ServiceInstance<T>> provider) {
        this.discovery = discovery;
        this.provider = provider;
    }

    @Override
    protected synchronized void startUp() throws Exception {

        register();

    }

    @Override
    protected synchronized void shutDown() throws Exception {
        unregister();
    }

    @VisibleForTesting
    synchronized void register() throws Exception {
        ServiceInstance<T> service = provider.get();

        checkState(service != null, "Null providers are not accepted.");

        if (instance.compareAndSet(null, service)) {
            LOG.debug("Registering service: {}", service);

            discovery.registerService(service);
        } else {
            LOG.warn("Service already registered! {}", service);
        }
    }

    @VisibleForTesting
    synchronized void unregister() throws Exception {
        ServiceInstance<T> service = instance.getAndSet(null);

        if (service != null) {
            LOG.debug("Unregistering service: {}", service);

            discovery.unregisterService(service);
        } else {
            LOG.debug("No service to unregister.");
        }
    }
}
