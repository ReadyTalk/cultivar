package com.readytalk.cultivar.discovery;

import static org.mockito.Mockito.verify;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.VerifyException;
import com.google.inject.util.Providers;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRegistrationServiceTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private ServiceInstance<Void> instance;

    @Mock
    private ServiceDiscovery<Void> discovery;

    private DefaultRegistrationService<Void> discoveryRegistration;

    @Before
    public void setUp() {
        discoveryRegistration = new DefaultRegistrationService<Void>(discovery, Providers.of(instance));
    }

    @Test
    public void startUp_RegistersService() throws Exception {
        discoveryRegistration.startUp();
        verify(discovery).registerService(instance);
    }

    @Test
    public void startUp_NullProvider_ThrowsISE() throws Exception {
        thrown.expect(IllegalStateException.class);

        new DefaultRegistrationService<Void>(discovery, Providers.<ServiceInstance<Void>> of(null)).startUp();
    }

    @Test
    public void shutDown_AfterStartUp_UnregistersService() throws Exception {
        discoveryRegistration.startUp();

        discoveryRegistration.shutDown();

        verify(discovery).unregisterService(instance);
    }

    @Test
    public void shutDown_WithoutStartup_ThrowsVerifyException() throws Exception {
        thrown.expect(VerifyException.class);

        discoveryRegistration.shutDown();
    }
}
