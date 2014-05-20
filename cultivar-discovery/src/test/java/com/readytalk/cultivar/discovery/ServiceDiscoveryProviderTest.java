package com.readytalk.cultivar.discovery;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServiceDiscoveryProviderTest {

    @Mock
    private ServiceDiscoveryBuilder<Object> builder;

    @Mock
    private ServiceDiscovery<Object> serviceDiscovery;

    @Mock
    private CuratorFramework client;

    @Mock
    private InstanceSerializer<Object> serializer;

    private ServiceDiscoveryProvider<Object> provider;

    @Before
    public void setUp() {
        when(builder.build()).thenReturn(serviceDiscovery);

        provider = new ServiceDiscoveryProvider<Object>(builder);
    }

    @Test
    public void build_ReturnsBuilderResult() {
        assertEquals(serviceDiscovery, builder.build());
    }

    @Test
    public void setClient_Delegates() {
        provider.setClient(client);

        verify(builder).client(client);
    }

    @Test
    public void setBasePath_Delegates() {
        provider.setBasePath("");

        verify(builder).basePath("");
    }

    @Test
    public void setSerializer_Delegates() {
        provider.setSerializer(serializer);

        verify(builder).serializer(serializer);
    }

}
