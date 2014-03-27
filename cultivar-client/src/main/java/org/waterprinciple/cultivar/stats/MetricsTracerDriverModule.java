package org.waterprinciple.cultivar.stats;

import org.apache.curator.drivers.TracerDriver;

import com.google.common.annotations.Beta;
import com.google.inject.PrivateModule;
import com.google.inject.name.Names;

@Beta
public class MetricsTracerDriverModule extends PrivateModule {

    private final String prefix;

    public MetricsTracerDriverModule() {
        this("curator.client");
    }

    public MetricsTracerDriverModule(final String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected void configure() {
        bind(TracerDriver.class).to(MetricsTracerDriver.class);
        bindConstant().annotatedWith(Names.named("Cultivar.Metrics.prefix")).to(prefix);

        expose(TracerDriver.class);
    }
}
