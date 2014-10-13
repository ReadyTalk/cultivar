package com.readytalk.cultivar.discovery;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.details.InstanceSerializer;

import com.google.common.annotations.Beta;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.readytalk.cultivar.internal.Private;

/**
 * Creates a ServiceDiscovery instance.
 */
@Beta
@NotThreadSafe
class ServiceDiscoveryProvider<T> implements Provider<ServiceDiscovery<T>> {

    private final ServiceDiscoveryBuilder<T> builder;

    @Inject
    ServiceDiscoveryProvider(final ServiceDiscoveryBuilder<T> builder) {
        this.builder = builder;
    }

    @Inject
    public void setClient(@Private final CuratorFramework framework) {
        this.builder.client(framework);
    }

    @Inject
    public void setBasePath(@Named("Cultivar.private.basePath") final String basePath) {
        this.builder.basePath(basePath);
    }

    @Inject(optional = true)
    public void setSerializer(final InstanceSerializer<T> serializer) {
        this.builder.serializer(serializer);
    }

    @Override
    public ServiceDiscovery<T> get() {
        return builder.build();
    }
}
