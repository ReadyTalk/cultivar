package com.readytalk.cultivar.stats;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.curator.drivers.TracerDriver;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.util.Types;
import com.readytalk.cultivar.Curator;

public class MetricsModuleTest {

    @Test
    public void getInstance_TracerDriver_ReturnsValue() {
        TracerDriver driver = Guice.createInjector(new MetricsModule()).getInstance(
                Key.get(TracerDriver.class, Curator.class));

        assertNotNull(driver);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getInstance_SetOfConnectionStateListeners_ContainsConnectionStateMetricsListener() {

        Injector inj = Guice.createInjector(new MetricsModule());

        assertTrue(((Set<ConnectionStateListener>) inj.getInstance(Key.get(Types.setOf(ConnectionStateListener.class))))
                .contains(inj.getInstance(ConnectionStateMetricsListener.class)));
    }
}
