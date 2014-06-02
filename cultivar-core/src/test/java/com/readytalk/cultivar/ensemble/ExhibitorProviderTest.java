package com.readytalk.cultivar.ensemble;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Properties;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.readytalk.cultivar.util.PropertyReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExhibitorProviderTest {
    @Mock
    private Properties props;

    private final Map<String, String> env = Maps.newHashMap();

    private ExhibitorProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new ExhibitorProvider();
    }

    @After
    public void tearDown() {
        PropertyReader.reset();
    }

    @Test
    public void get_PresentInProperties_ReturnsProperties() {
        PropertyReader.setProperties(ImmutableMap.of(ExhibitorProvider.PROPERTY_NAME, "test"));

        assertEquals(Optional.of("test"), provider.get());
    }

    @Test
    public void get_OverridenWithSetterAndPresentInPropertiesAndEnvironment_ReturnsOverride() {
        PropertyReader.setProperties(ImmutableMap.of(ExhibitorProvider.PROPERTY_NAME, "test"));

        provider.setDefault("conn");

        assertEquals(Optional.of("conn"), provider.get());
    }
}
