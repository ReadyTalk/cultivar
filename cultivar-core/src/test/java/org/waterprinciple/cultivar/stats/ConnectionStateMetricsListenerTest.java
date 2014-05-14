package org.waterprinciple.cultivar.stats;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionStateMetricsListenerTest {
    private static final String PREFIX = "prefix";

    @Mock
    private CuratorFramework framework;

    @Mock
    private MetricRegistry metrics;

    @Mock
    private Meter meter;

    private ConnectionStateMetricsListener listener;

    @Before
    public void setUp() throws Exception {
        listener = new ConnectionStateMetricsListener(PREFIX, metrics);

        when(metrics.meter(anyString())).thenReturn(meter);
    }

    @Test
    public void stateChanged_GivenState_MarksState() {
        listener.stateChanged(framework, ConnectionState.CONNECTED);

        verify(metrics).meter("prefix.connection.connected");

        verify(meter).mark();
    }
}
