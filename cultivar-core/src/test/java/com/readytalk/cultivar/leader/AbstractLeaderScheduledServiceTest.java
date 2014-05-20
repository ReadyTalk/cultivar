package com.readytalk.cultivar.leader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class AbstractLeaderScheduledServiceTest {

    @Mock
    private LeaderLatch latch;

    @Mock
    private Logger logger;

    private AbstractLeaderScheduledService service;

    @Before
    public void setUp() throws Exception {
        service = new AbstractLeaderScheduledService(latch) {
            @Override
            protected Scheduler scheduler() {
                // It doesn't matter so long as it won't run during the lifetime of the test.
                return Scheduler.newFixedDelaySchedule(30L, 30L, TimeUnit.MINUTES);
            }

            @Override
            protected void doRunOneIteration() throws Exception {
                logger.info("run");
            }

            @Override
            protected void doStartup() throws Exception {
                logger.info("startup");
            }

            @Override
            protected void doShutdown() throws Exception {
                logger.info("shutdown");
            }
        };
    }

    @Test
    public void runOneIteration_HasLeadership_RunsDoRunOneIteration() throws Exception {
        when(latch.hasLeadership()).thenReturn(true);

        service.runOneIteration();

        verify(logger).info("run");
    }

    @Test
    public void runOneIteration_DoesNotBlock() throws Exception {
        when(latch.hasLeadership()).thenReturn(false);

        service.runOneIteration();

        verify(latch, never()).await();
        verify(latch, never()).await(anyLong(), any(TimeUnit.class));
    }

    @Test
    public void startUp_StartsLatch() throws Exception {
        service.startUp();

        verify(latch).start();
    }

    @Test
    public void startUp_RunsDoStartupAfterStarting() throws Exception {
        service.startUp();

        InOrder order = inOrder(latch, logger);

        order.verify(latch).start();
        order.verify(logger).info("startup");
    }

    @Test
    public void shutDown_ClosesLatch() throws Exception {
        service.shutDown();

        verify(latch).close();
    }

    @Test
    public void shutDown_RunsDoShutdownBeforeStopping() throws Exception {
        service.shutDown();

        InOrder order = inOrder(latch, logger);

        order.verify(logger).info("shutdown");
        order.verify(latch).close();
    }

    @Test
    public void shutDown_DoShutdownThrowsException_ClosesLatch() throws Exception {
        RuntimeException thrownEx = new RuntimeException();

        doThrow(thrownEx).when(logger).info("shutdown");

        try {
            service.shutDown();
            fail("Did not throw exception.");
        } catch (RuntimeException ex) {
            assertEquals("Exception thrown was not the one that was expected.", thrownEx, ex);
        }

        verify(latch).close();
    }
}
