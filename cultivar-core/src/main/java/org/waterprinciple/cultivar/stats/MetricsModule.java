package org.waterprinciple.cultivar.stats;

import org.apache.curator.drivers.TracerDriver;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.Beta;
import com.google.inject.AbstractModule;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

@Beta
public class MetricsModule extends AbstractModule {

    private final String prefix;

    public MetricsModule() {
        this("curator.client");
    }

    public MetricsModule(final String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected void configure() {
        install(new PrivateModule() {
            @Override
            protected void configure() {
                requireBinding(MetricRegistry.class);

                bindConstant().annotatedWith(Names.named("Cultivar.Metrics.prefix")).to(prefix);

                bind(TracerDriver.class).to(MetricsTracerDriver.class);

                expose(TracerDriver.class);

                bind(ConnectionStateMetricsListener.class).in(Singleton.class);

                expose(ConnectionStateMetricsListener.class);
            }
        });

        Multibinder<ConnectionStateListener> listeners = Multibinder.newSetBinder(binder(),
                ConnectionStateListener.class);

        listeners.addBinding().to(ConnectionStateMetricsListener.class);

    }
}
