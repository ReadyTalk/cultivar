package org.waterprinciple.cultivar.discovery;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Provider;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceProviderBuilder;
import org.waterprinciple.cultivar.internal.Private;

import com.google.common.annotations.Beta;
import com.google.inject.Inject;

@Beta
@ThreadSafe
class ServiceProviderBuilderProvider<T> implements Provider<ServiceProviderBuilder<T>> {

    private final ServiceDiscovery<T> discovery;

    @Inject
    ServiceProviderBuilderProvider(@Private final ServiceDiscovery<T> discovery) {
        this.discovery = discovery;
    }

    @Override
    public ServiceProviderBuilder<T> get() {
        return discovery.serviceProviderBuilder();
    }
}
