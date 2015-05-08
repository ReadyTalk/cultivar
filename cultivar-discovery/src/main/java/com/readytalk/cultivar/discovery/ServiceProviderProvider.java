package com.readytalk.cultivar.discovery;

import java.util.Set;
import java.util.concurrent.ThreadFactory;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.curator.x.discovery.DownInstancePolicy;
import org.apache.curator.x.discovery.InstanceFilter;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.ServiceProviderBuilder;

import com.google.common.annotations.Beta;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.readytalk.cultivar.internal.Private;

/**
 * Generates a ServiceProvider instance.
 */
@Beta
@NotThreadSafe
class ServiceProviderProvider<T> implements Provider<ServiceProvider<T>> {

    private final ServiceProviderBuilder<T> builder;

    @Inject
    ServiceProviderProvider(@Private final ServiceProviderBuilder<T> builder) {
        this.builder = builder;
    }

    @Inject
    public synchronized void setServiceName(@Private final String name) {
        builder.serviceName(name);
    }

    @Inject(optional = true)
    public synchronized void setDownInstancePolicy(@Private final DownInstancePolicy policy) {
        builder.downInstancePolicy(policy);
    }

    @Inject(optional = true)
    public synchronized void setInstanceFilters(@Private final Set<InstanceFilter<T>> filters) {
        for (InstanceFilter<T> o : filters) {
            builder.additionalFilter(o);

        }
    }

    @Inject(optional = true)
    public synchronized void setProviderStrategy(@Private final ProviderStrategy<T> strategy) {
        builder.providerStrategy(strategy);
    }

    @Inject(optional = true)
    public synchronized void setThreadFactory(@Private final ThreadFactory threadFactory) {
        builder.threadFactory(threadFactory);
    }

    @Override
    public synchronized ServiceProvider<T> get() {
        return new ServiceProviderWrapper<T>(builder);
    }
}
