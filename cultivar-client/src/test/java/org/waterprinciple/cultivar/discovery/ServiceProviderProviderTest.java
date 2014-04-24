package org.waterprinciple.cultivar.discovery;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ThreadFactory;

import org.apache.curator.x.discovery.DownInstancePolicy;
import org.apache.curator.x.discovery.InstanceFilter;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.ServiceProviderBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;

@RunWith(MockitoJUnitRunner.class)
public class ServiceProviderProviderTest {
    @Mock
    private ServiceProviderBuilder<Void> builder;

    @Mock
    private ServiceProvider<Void> provider;

    @Mock
    private InstanceFilter<Void> filter1;

    @Mock
    private InstanceFilter<Void> filter2;

    @Mock
    private DownInstancePolicy downInstancePolicy;

    @Mock
    private ProviderStrategy<Void> providerStrategy;

    @Mock
    private ThreadFactory threadFactory;

    private ServiceProviderProvider<Void> providerProvider;

    @Before
    public void setUp() throws Exception {
        providerProvider = new ServiceProviderProvider<Void>(builder);

        when(builder.build()).thenReturn(provider);
    }

    @Test
    public void get_ReturnsDelegate() {
        assertEquals(provider, providerProvider.get());
    }

    @Test
    public void setDownInstancePolicy_Delegate() {
        providerProvider.setDownInstancePolicy(downInstancePolicy);

        verify(builder).downInstancePolicy(downInstancePolicy);
    }

    @Test
    public void setInstanceFilters_DelegatesAll() {
        providerProvider.setInstanceFilters(ImmutableSet.of(filter1, filter2));

        verify(builder).additionalFilter(filter1);
        verify(builder).additionalFilter(filter2);
    }

    @Test
    public void setProviderStrategy_Delegates() {
        providerProvider.setProviderStrategy(providerStrategy);

        verify(builder).providerStrategy(providerStrategy);
    }

    @Test
    public void setThreadFactory_Delegates() {
        providerProvider.setThreadFactory(threadFactory);

        verify(builder).threadFactory(threadFactory);
    }

    @Test
    public void setServiceName_Delegates() {
        providerProvider.setServiceName("");

        verify(builder).serviceName("");
    }
}
