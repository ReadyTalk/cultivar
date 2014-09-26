package com.readytalk.cultivar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.util.concurrent.Executor;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCultivarStartStopManagerTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private Logger logger;

    @Mock
    private CuratorManagementService curatorManagementService;

    private ServiceManager serviceManager;

    private DefaultCultivarStartStopManager startStopManager;

    @Before
    public void setUp() {
        /*
         * Multiple things in AbstractIdleService and ServiceManager are final, necessitating indirect methods of seeing
         * if the service has been started up or shut down.
         */
        Service service = new AbstractIdleService() {
            @Override
            protected void startUp() throws Exception {
                logger.info("startUp");

            }

            @Override
            protected void shutDown() throws Exception {
                logger.info("shutDown");
            }

            @Override
            protected Executor executor() {
                return MoreExecutors.directExecutor();
            }
        };

        serviceManager = new ServiceManager(ImmutableSet.of(service));

        when(curatorManagementService.startAsync()).thenReturn(curatorManagementService);
        when(curatorManagementService.stopAsync()).thenReturn(curatorManagementService);

        startStopManager = new DefaultCultivarStartStopManager(curatorManagementService, serviceManager);
    }

    @Test
    public void startUp_StartsCuratorThenServices() throws Exception {
        startStopManager.startUp();

        InOrder order = inOrder(curatorManagementService, logger);

        order.verify(curatorManagementService).startAsync();
        order.verify(curatorManagementService).awaitRunning();
        order.verify(logger).info("startUp");
    }

    @Test
    public void startUp_ManagementService_ThrowsException_RethrowsException() {
        Exception toCatch = new Exception();

        when(curatorManagementService.failureCause()).thenReturn(toCatch);
        doThrow(new IllegalStateException()).when(curatorManagementService).awaitRunning();

        try {
            startStopManager.startUp();
            fail("No exception thrown.");
        } catch (Exception ex) {
            assertEquals("Exception caught is not failure cause.", toCatch, ex);
        }

    }

    @Test
    public void shutDown_ManagementService_ThrowsException_RethrowsException() {
        Exception toCatch = new Exception();

        when(curatorManagementService.failureCause()).thenReturn(toCatch);
        doThrow(new IllegalStateException()).when(curatorManagementService).awaitTerminated();

        try {
            startStopManager.shutDown();
            fail("No exception thrown.");
        } catch (Exception ex) {
            assertEquals("Exception caught is not failure cause.", toCatch, ex);
        }

    }

    @Test
    public void startUp_ManagementService_AwaitThrowsISEWithoutCuratorException_ThrowsISE() {
        Exception toCatch = new IllegalArgumentException();

        when(curatorManagementService.failureCause()).thenThrow(new IllegalStateException());
        doThrow(toCatch).when(curatorManagementService).awaitRunning();

        try {
            startStopManager.startUp();
            fail("No exception thrown.");
        } catch (Exception ex) {
            assertEquals("Exception caught is not failure cause.", toCatch, ex);
        }

    }

    @Test
    public void shutDown_ManagementService_AwaitThrowsISEWithoutCuratorException_ThrowsISE() {
        Exception toCatch = new IllegalArgumentException();

        when(curatorManagementService.failureCause()).thenThrow(new IllegalStateException());
        doThrow(toCatch).when(curatorManagementService).awaitTerminated();

        try {
            startStopManager.shutDown();
            fail("No exception thrown.");
        } catch (Exception ex) {
            assertEquals("Exception caught is not failure cause.", toCatch, ex);
        }

    }

    @Test
    public void startUp_ServiceManager_StartsTwice_ThrowsISE() throws Exception {

        thrown.expect(IllegalStateException.class);

        // Starting it ensures that it will throw exception for starting twice.
        serviceManager.startAsync().awaitHealthy();

        startStopManager.startUp();
    }

    @Test
    public void startUp_ServiceManager_ServiceThrowsExceptionOnStartup_ThrowsISE() throws Exception {
        thrown.expect(IllegalStateException.class);

        final Throwable ex = new IllegalArgumentException();

        doThrow(ex).when(logger).info("startUp");

        startStopManager.startUp();
    }

    @Test
    public void shutDown_ServiceManager_ServiceThrowsExceptionOnShutdown_DoesNotRethrow() throws Exception {
        startStopManager.startUp();

        final Throwable ex = new IllegalArgumentException();

        doThrow(ex).when(logger).info("shutDown");

        startStopManager.shutDown();
    }

    @Test
    public void shutDown_ShutsDownServicesThenCurator() throws Exception {
        serviceManager.startAsync().awaitHealthy();
        startStopManager.shutDown();

        InOrder order = inOrder(curatorManagementService, logger);

        order.verify(logger).info("shutDown");
        order.verify(curatorManagementService).stopAsync();
    }
}
