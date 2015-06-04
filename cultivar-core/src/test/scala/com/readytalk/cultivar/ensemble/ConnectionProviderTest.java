package com.readytalk.cultivar.ensemble;

import static org.junit.Assert.assertEquals;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.readytalk.cultivar.util.PropertyReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionProviderTest {

    private ConnectionProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new ConnectionProvider();
    }

    @After
    public void tearDown() {
        PropertyReader.reset();
    }

    @Test
    public void get_PresentInPropertiesAndEnvironment_ReturnsProperties() {
        PropertyReader.setProperties(ImmutableMap.of(ConnectionProvider.PROPERTY_NAME, "test"));

        assertEquals(Optional.of("test"), provider.get());
    }

    @Test
    public void get_OverridenWithSetterAndPresentInPropertiesAndEnvironment_ReturnsOverride() {
        PropertyReader.setProperties(ImmutableMap.of(ConnectionProvider.PROPERTY_NAME, "test"));

        provider.setDefault("conn");

        assertEquals(Optional.of("conn"), provider.get());
    }
}
