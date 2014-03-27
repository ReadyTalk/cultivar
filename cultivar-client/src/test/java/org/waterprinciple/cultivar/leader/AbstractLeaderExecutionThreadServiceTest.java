package org.waterprinciple.cultivar.leader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class AbstractLeaderExecutionThreadServiceTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private LeaderLatch latch;

    @Mock
    private Logger logger;

    private AbstractLeaderExecutionThreadService service;

    @Before
    public void setUp() throws Exception {
        service = new AbstractLeaderExecutionThreadService(latch) {
            @Override
            protected void doRun() {
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
    public void runWithLatch_OnInterruption_ThrowsInterruptedException() throws Exception {
        thrown.expect(InterruptedException.class);

        doThrow(InterruptedException.class).when(latch).await();

        service.runWithLatch();
    }

    @Test
    public void runWithLatch_NoInterruption_LatchAwaitsFirst() throws Exception {
        service.runWithLatch();

        InOrder order = inOrder(latch, logger);

        order.verify(latch).await();
        order.verify(logger).info("run");
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
