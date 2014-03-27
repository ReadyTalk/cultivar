package org.waterprinciple.cultivar.stats;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MetricsTracerDriverTest {

    @Mock
    private MetricRegistry metrics;

    @Mock
    private Timer timer;

    @Mock
    private Meter meter;

    private MetricsTracerDriver tracer;

    @Before
    public void setUp() throws Exception {
        when(metrics.meter(anyString())).thenReturn(meter);
        when(metrics.timer(anyString())).thenReturn(timer);

        tracer = new MetricsTracerDriver("prefix", metrics);
    }

    @Test
    public void addTrace_WithName_AddsPrefix() {
        tracer.addTrace("trace", 10L, TimeUnit.MILLISECONDS);

        verify(metrics).timer("prefix.trace");
    }

    @Test
    public void addTrace_WithTime_Delegates() {
        tracer.addTrace("trace", 10L, TimeUnit.MILLISECONDS);

        verify(timer).update(10L, TimeUnit.MILLISECONDS);
    }

    @Test
    public void addCount_WithName_AddsPrefix() {
        tracer.addCount("count", 2);

        verify(metrics).meter("prefix.count");
    }

    @Test
    public void addCount_WithIncrement_Delegates() {
        tracer.addCount("count", 2);

        verify(meter).mark(2);
    }
}
