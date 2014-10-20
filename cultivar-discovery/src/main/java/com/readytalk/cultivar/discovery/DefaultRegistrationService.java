package com.readytalk.cultivar.discovery;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Verify.verifyNotNull;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;

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

    private final ServiceDiscovery<T> discovery;
    private final Provider<ServiceInstance<T>> provider;

    private final AtomicReference<ServiceInstance<T>> instance = Atomics.newReference();

    @Inject
    DefaultRegistrationService(@Private final ServiceDiscovery<T> discovery,
            @Private final Provider<ServiceInstance<T>> provider) {
        this.discovery = discovery;
        this.provider = provider;
    }

    @Override
    protected void startUp() throws Exception {

        ServiceInstance<T> service = provider.get();

        checkState(service != null, "Null providers are not accepted.");

        instance.set(provider.get());

        discovery.registerService(instance.get());
    }

    @Override
    protected void shutDown() throws Exception {
        ServiceInstance<T> service = instance.getAndSet(null);

        verifyNotNull(service, "Service has not been set. Should never happen.");

        discovery.unregisterService(service);
    }
}
