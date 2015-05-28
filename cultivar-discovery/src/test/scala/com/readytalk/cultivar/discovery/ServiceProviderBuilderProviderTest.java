package com.readytalk.cultivar.discovery;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceProviderBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServiceProviderBuilderProviderTest {

    @Mock
    private ServiceDiscovery<Void> discovery;

    @Mock
    private ServiceProviderBuilder<Void> providerBuilder;

    private ServiceProviderBuilderProvider<Void> provider;

    @Before
    public void setUp() {
        provider = new ServiceProviderBuilderProvider<Void>(discovery);

        when(discovery.serviceProviderBuilder()).thenReturn(providerBuilder);
    }

    @Test
    public void get_ReturnsDelegate() {
        assertEquals(providerBuilder, provider.get());
    }
}
