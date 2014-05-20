package com.readytalk.cultivar.stats;

import static com.codahale.metrics.MetricRegistry.name;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.curator.drivers.TracerDriver;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.annotations.Beta;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Reports the trace information from Curator to a MetricsRegistry.
 */
@Beta
@ThreadSafe
public class MetricsTracerDriver implements TracerDriver {

    private final String prefix;
    private final MetricRegistry metrics;

    @Inject
    MetricsTracerDriver(@Named("Cultivar.Metrics.prefix") final String prefix, final MetricRegistry metrics) {
        this.prefix = name(prefix, "trace");
        this.metrics = metrics;
    }

    @Override
    public void addTrace(final String name, final long time, final TimeUnit unit) {
        Timer timer = metrics.timer(name(prefix, name));

        timer.update(time, unit);
    }

    @Override
    public void addCount(final String name, final int increment) {
        Meter meter = metrics.meter(name(prefix, name));

        meter.mark(increment);
    }
}
