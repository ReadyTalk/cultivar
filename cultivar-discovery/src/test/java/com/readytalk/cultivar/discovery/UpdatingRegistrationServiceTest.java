package com.readytalk.cultivar.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

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
    public void runOneIteration_NoExceptions_UpdatesDiscovery() throws Exception {
        service.runOneIteration();

        verify(discovery).updateService(instance);
    }

    @Test
    public void runOneIteration_ExceptionInDiscovery_LogsException() throws Exception {
        doThrow(Exception.class).when(discovery).updateService(instance);

        service.runOneIteration();

        verify(log).warn(anyString(), any(Exception.class));
    }

    @Test
    public void runOneIteration_InterruptedExceptionInDiscovery_SetsInterruptFlag() throws Exception {
        doThrow(InterruptedException.class).when(discovery).updateService(instance);

        service.runOneIteration();

        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    public void runOneIteration_NullFromProvider_LogsVerifyException() throws Exception {
        new UpdatingRegistrationService<Void>(discovery, Providers.<ServiceInstance<Void>> of(null), scheduler,
                executorService, log).runOneIteration();

        verify(log).warn(anyString(), any(VerifyException.class));
    }
}
