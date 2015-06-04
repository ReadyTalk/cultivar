package com.readytalk.cultivar.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.concurrent.ScheduledExecutorService;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.google.common.base.VerifyException;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.util.Providers;

@RunWith(MockitoJUnitRunner.class)
public class UpdatingRegistrationServiceTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private ServiceDiscovery<Void> discovery;

    @Mock
    private ServiceInstance<Void> instance;

    @Mock
    private AbstractScheduledService.Scheduler scheduler;

    @Mock
    private ScheduledExecutorService executorService;

    @Mock
    private Logger log;

    private UpdatingRegistrationService<Void> service;

    @Before
    public void setUp() {
        service = new UpdatingRegistrationService<Void>(discovery, Providers.of(instance), scheduler, executorService,
                log);

    }

    @After
    public void tearDown() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            // Clearing interrupt.
        }
    }

    @Test
    public void startUp_Instance_RegistersWithDiscovery() throws Exception {
        service.startUp();

        verify(discovery).registerService(instance);
    }

    @Test
    public void startUp_Null_ThrowsVerifyException() throws Exception {
        thrown.expect(VerifyException.class);

        new UpdatingRegistrationService<Void>(discovery, Providers.<ServiceInstance<Void>> of(null), scheduler,
                executorService, log).startUp();
    }

    @Test
    public void scheduler_IsSameAsConstructor() {
        assertEquals(scheduler, service.scheduler());
    }

    @Test
    public void executor_IsSameAsConstructor() {
        assertEquals(executorService, service.executor());
    }

    @Test
    public void runOneIteration_NotRegistered_DoesNothing() throws Exception {
        service.runOneIteration();

        verifyZeroInteractions(discovery);
    }

    @Test
    public void startUp_ThenUpdate_AllowsUpdate() throws Exception {
        service.startUp();

        service.runOneIteration();

        verify(discovery).updateService(instance);
    }

    @Test
    public void register_ServiceAlreadyRegistered_LogsWarning() throws Exception {
        service.setRegistered(true);

        service.register();

        verify(log).warn(anyString(), anyObject());
    }

    @Test
    public void unregister_AllowsRegistration() throws Exception {
        service.setRegistered(true);

        service.unregister();

        service.register();

        verify(discovery).registerService(instance);
    }

    @Test
    public void runOneIteration_Registered_NoExceptions_UpdatesDiscovery() throws Exception {
        service.setRegistered(true);

        service.runOneIteration();

        verify(discovery).updateService(instance);
    }

    @Test
    public void runOneIteration_Registered_ExceptionInDiscovery_LogsException() throws Exception {
        doThrow(Exception.class).when(discovery).updateService(instance);

        service.setRegistered(true);

        service.runOneIteration();

        verify(log).warn(anyString(), any(Exception.class));
    }

    @Test
    public void runOneIteration_Registered_InterruptedExceptionInDiscovery_SetsInterruptFlag() throws Exception {
        doThrow(InterruptedException.class).when(discovery).updateService(instance);

        service.setRegistered(true);

        service.runOneIteration();

        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    public void runOneIteration_Registered_NullFromProvider_LogsVerifyException() throws Exception {
        UpdatingRegistrationService<Void> nullProviderService = new UpdatingRegistrationService<Void>(discovery,
                Providers.<ServiceInstance<Void>> of(null), scheduler, executorService, log);

        nullProviderService.setRegistered(true);

        nullProviderService.runOneIteration();

        verify(log).warn(anyString(), any(VerifyException.class));
    }
}
