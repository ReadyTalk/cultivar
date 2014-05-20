package com.readytalk.cultivar.discovery;

import static org.mockito.Mockito.verify;

import org.apache.curator.x.discovery.ServiceProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServiceProviderManagerTest {

    @Mock
    private ServiceProvider<Void> provider;

    private ServiceProviderManager<Void> manager;

    @Before
    public void setUp() throws Exception {
        manager = new ServiceProviderManager<Void>(provider);
    }

    @Test
    public void startUp_StartsDelegate() throws Exception {
        manager.startUp();

        verify(provider).start();
    }

    @Test
    public void shutDown_ClosesDelegate() throws Exception {
        manager.shutDown();

        verify(provider).close();
    }
}
