package org.waterprinciple.cultivar.stats;

import static org.junit.Assert.assertNotNull;

import org.apache.curator.drivers.TracerDriver;
import org.junit.Test;

import com.google.inject.Guice;

public class MetricsTracerDriverModuleTest {

    @Test
    public void getInstance_TracerDriver_ReturnsValue() {
        TracerDriver driver = Guice.createInjector(new MetricsTracerDriverModule()).getInstance(TracerDriver.class);

        assertNotNull(driver);
    }
}
