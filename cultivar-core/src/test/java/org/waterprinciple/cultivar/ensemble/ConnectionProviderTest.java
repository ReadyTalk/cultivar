package org.waterprinciple.cultivar.ensemble;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionProviderTest {

    @Mock
    private Properties props;

    private final Map<String, String> env = Maps.newHashMap();

    private ConnectionProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new ConnectionProvider(props, env);
    }

    @Test
    public void get_PresentInPropertiesAndEnvironment_ReturnsProperties() {
        when(props.getProperty(ConnectionProvider.PROPERTY_NAME)).thenReturn("test");
        env.put(ConnectionProvider.ENVIRONMENT_NAME, "badvalue");

        assertEquals(Optional.of("test"), provider.get());
    }

    @Test
    public void get_OverridenWithSetterAndPresentInPropertiesAndEnvironment_ReturnsOverride() {
        when(props.getProperty(ConnectionProvider.PROPERTY_NAME)).thenReturn("test");
        env.put(ConnectionProvider.ENVIRONMENT_NAME, "badvalue");

        provider.setDefault("conn");

        assertEquals(Optional.of("conn"), provider.get());
    }

    @Test
    public void get_PresentInEnvironment_ReturnsEnvironment() {
        when(props.getProperty(ConnectionProvider.PROPERTY_NAME)).thenReturn(null);
        env.put(ConnectionProvider.ENVIRONMENT_NAME, "test");

        assertEquals(Optional.of("test"), provider.get());
    }
}
