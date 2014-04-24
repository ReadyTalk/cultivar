package org.waterprinciple.cultivar.discovery;

import static org.mockito.Mockito.verify;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServiceDiscoveryManagerTest {

    @Mock
    private ServiceDiscovery<Void> discovery;

    private ServiceDiscoveryManager<Void> manager;

    @Before
    public void setUp() {
        manager = new ServiceDiscoveryManager<Void>(discovery);
    }

    @Test
    public void startUp_DelegatesToStart() throws Exception {
        manager.startUp();

        verify(discovery).start();
    }

    @Test
    public void shutDown_DelegatesToClose() throws Exception {
        manager.shutDown();

        verify(discovery).close();
    }
}
