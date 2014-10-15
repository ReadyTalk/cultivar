package com.readytalk.cultivar.discovery;

import static com.google.common.base.Verify.verifyNotNull;

import java.util.concurrent.ScheduledExecutorService;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.readytalk.cultivar.internal.Private;

/**
 * A version of the RegistrationService that updates based on a provider on a regular schedule. If there is ane
 * exception coming from the discovery system it will log it, and if it is an InterruptedException it will interrupt the
 * thread.
 *
 * @param <T>
 *            The type of payload for discovery.
 */
@Beta
class UpdatingRegistrationService<T> extends AbstractScheduledService implements RegistrationService<T> {

    private final Logger log;

    private final ServiceDiscovery<T> discovery;
    private final Provider<ServiceInstance<T>> provider;

    private final Scheduler scheduler;
    private final ScheduledExecutorService executorService;

    @Inject
    UpdatingRegistrationService(@Private final ServiceDiscovery<T> discovery,
            @Private final Provider<ServiceInstance<T>> provider, @Private final Scheduler scheduler,
            @Private final ScheduledExecutorService executorService) {
        this(discovery, provider, scheduler, executorService, LoggerFactory
                .getLogger(UpdatingRegistrationService.class));
    }

    UpdatingRegistrationService(@Private final ServiceDiscovery<T> discovery,
            @Private final Provider<ServiceInstance<T>> provider, @Private final Scheduler scheduler,
            @Private final ScheduledExecutorService executorService, final Logger log) {
        this.discovery = discovery;
        this.provider = provider;

        this.scheduler = scheduler;

        this.executorService = executorService;

        this.log = log;
    }

    @Override
    protected void startUp() throws Exception {

        discovery.registerService(verifyNotNull(provider.get(), "Provider should not return null."));
    }

    @Override
    protected void shutDown() throws Exception {
        discovery.unregisterService(provider.get());
    }

    @Override
    protected Scheduler scheduler() {
        return scheduler;
    }

    @Override
    protected void runOneIteration() throws Exception {

        ServiceInstance<T> instance = null;
        try {
            instance = verifyNotNull(provider.get());

            discovery.updateService(instance);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted for service: " + String.valueOf(instance), ex);
        } catch (Exception ex) {
            log.warn("Exception while updating service: " + String.valueOf(instance), ex);
        }
    }

    @Override
    protected ScheduledExecutorService executor() {
        return this.executorService;
    }
}
