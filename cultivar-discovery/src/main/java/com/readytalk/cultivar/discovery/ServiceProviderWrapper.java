package com.readytalk.cultivar.discovery;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.annotations.VisibleForTesting;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.ServiceProviderBuilder;

/**
 * Working around CURATOR-169.
 */
@ThreadSafe
class ServiceProviderWrapper<T> implements ServiceProvider<T> {

    private final ServiceProviderBuilder<T> builder;

    private volatile ServiceProvider<T> value = null;

    ServiceProviderWrapper(final ServiceProviderBuilder<T> builder) {
        this.builder = builder;
    }

    private ServiceProvider<T> delegate() {

        if (value == null) {
            synchronized (this) {
                if (value == null) {
                    value = builder.build();
                }
            }
        }

        return value;
    }

    @Override
    public void start() throws Exception {
        delegate().start();
    }

    @Override
    public ServiceInstance<T> getInstance() throws Exception {
        return delegate().getInstance();
    }

    @Override
    public Collection<ServiceInstance<T>> getAllInstances() throws Exception {
        return delegate().getAllInstances();
    }

    @Override
    public void noteError(final ServiceInstance<T> instance) {
        delegate().noteError(instance);
    }

    @Override
    public void close() throws IOException {
        delegate().close();
    }

    @VisibleForTesting
    ServiceProvider<T> get() {
        return delegate();
    }
}
