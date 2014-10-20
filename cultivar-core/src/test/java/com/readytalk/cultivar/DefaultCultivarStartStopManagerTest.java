package com.readytalk.cultivar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
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
    private Logger extraLogger1;

    @Mock
    private Logger extraLogger2;

    @Mock
    private CuratorManagementService curatorManagementService;

    private ServiceManager serviceManager;

    private ServiceManager extraServiceManager1;

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

        Service extraService1 = new AbstractIdleService() {
            @Override
            protected void startUp() throws Exception {
                extraLogger1.info("startUp");

            }

            @Override
            protected void shutDown() throws Exception {
                extraLogger1.info("shutDown");
            }

            @Override
            protected Executor executor() {
                return MoreExecutors.directExecutor();
            }
        };

        Service extraService2 = new AbstractIdleService() {
            @Override
            protected void startUp() throws Exception {
                extraLogger2.info("startUp");

            }

            @Override
            protected void shutDown() throws Exception {
                extraLogger2.info("shutDown");
            }

            @Override
            protected Executor executor() {
                return MoreExecutors.directExecutor();
            }
        };

        serviceManager = new ServiceManager(ImmutableSet.of(service));

        extraServiceManager1 = new ServiceManager(ImmutableSet.of(extraService1));

        ServiceManager extraServiceManager2 = new ServiceManager(ImmutableSet.of(extraService2));

        when(curatorManagementService.startAsync()).thenReturn(curatorManagementService);
        when(curatorManagementService.stopAsync()).thenReturn(curatorManagementService);

        startStopManager = new DefaultCultivarStartStopManager(curatorManagementService, serviceManager,
                ImmutableSet.of(extraServiceManager1, extraServiceManager2));
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

    @Test
    public void startUp_StartsServicesThenExtraServices() throws Exception {
        startStopManager.startUp();

        InOrder order = inOrder(logger, extraLogger1);
        order.verify(logger).info("startUp");
        order.verify(extraLogger1).info("startUp");
    }

    @Test
    public void shutDown_StopsExtraServicesThenServices() throws Exception {
        startStopManager.startUp();

        startStopManager.shutDown();

        InOrder order = inOrder(logger, extraLogger1);

        order.verify(extraLogger1).info("shutDown");
        order.verify(logger).info("shutDown");
    }

    @Test
    public void startUp_ExceptionInExtraServiceStartAsync_Continues() throws Exception {
        extraServiceManager1.startAsync().awaitHealthy();

        reset(extraLogger1);

        startStopManager.startUp();

        verify(extraLogger1, never()).info("startUp");
        verify(extraLogger2).info("startUp");

    }

    @Test
    public void startUp_ExceptionInExtraService_Continues() throws Exception {
        doThrow(new RuntimeException()).when(extraLogger1).info("startUp");

        startStopManager.startUp();

        verify(extraLogger2).info("startUp");
    }

    @Test
    public void shutDown_ExceptionInExtraServiceStopAsync_Continues() throws Exception {
        startStopManager.startUp();

        extraServiceManager1.stopAsync().awaitStopped();

        reset(extraLogger1);

        startStopManager.shutDown();

        verify(extraLogger1, never()).info("shutDown");
        verify(extraLogger2).info("shutDown");

    }

    @Test
    public void shutDown_ExceptionInExtraService_Continues() throws Exception {
        doThrow(new RuntimeException()).when(extraLogger1).info("shutDown");

        startStopManager.startUp();

        startStopManager.shutDown();

        verify(extraLogger2).info("shutDown");
        verify(logger).info("shutDown");
    }
}
