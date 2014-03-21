package org.waterprinciple.cultivar;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.util.concurrent.Executor;

import org.junit.Before;
import org.junit.Test;
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
                return MoreExecutors.sameThreadExecutor();
            }
        };

        serviceManager = new ServiceManager(ImmutableSet.of(service));

        when(curatorManagementService.startAsync()).thenReturn(curatorManagementService);
        when(curatorManagementService.stopAsync()).thenReturn(curatorManagementService);

        startStopManager = new DefaultCultivarStartStopManager(curatorManagementService, serviceManager);
    }

    @Test
    public void startUp_StartsCuratorThenServices() {
        startStopManager.startUp();

        InOrder order = inOrder(curatorManagementService, logger);

        order.verify(curatorManagementService).startAsync();
        order.verify(curatorManagementService).awaitRunning();
        order.verify(logger).info("startUp");
    }

    @Test
    public void shutDown_ShutsDownServicesThenCurator() {
        serviceManager.startAsync().awaitHealthy();
        startStopManager.shutDown();

        InOrder order = inOrder(curatorManagementService, logger);

        order.verify(logger).info("shutDown");
        order.verify(curatorManagementService).stopAsync();
    }
}
