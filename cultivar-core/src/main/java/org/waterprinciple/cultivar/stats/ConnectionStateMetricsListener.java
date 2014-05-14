package org.waterprinciple.cultivar.stats;

import static com.codahale.metrics.MetricRegistry.name;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.Beta;
import com.google.common.base.Ascii;
import com.google.inject.Inject;
import com.google.inject.name.Named;

@Beta
@ThreadSafe
public class ConnectionStateMetricsListener implements ConnectionStateListener {

    private final String prefix;
    private final MetricRegistry metrics;

    @Inject
    ConnectionStateMetricsListener(@Named("Cultivar.Metrics.prefix") final String prefix, final MetricRegistry metrics) {
        this.prefix = name(prefix, "connection");
        this.metrics = metrics;
    }

    @Override
    public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
        metrics.meter(name(prefix, Ascii.toLowerCase(newState.name()))).mark();

    }
}
