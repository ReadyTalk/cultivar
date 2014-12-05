package com.readytalk.cultivar.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(Enclosed.class)
public class CuratorUninterruptiblesTest {

    @RunWith(MockitoJUnitRunner.class)
    public static class CuratorFrameworkTest {
        @Rule
        public final Timeout timeout = new Timeout(10, TimeUnit.SECONDS);

        @Mock
        private CuratorFramework framework;

        @After
        public void tearDown() {
            // Clear the interrupted flag.
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                // Deliberately ignore.
            }
        }

        @Test
        public void blockUntilConnected_NoArgs_NoInterrupt_Delegates_NoThreadInterrupt() throws Exception {
            CuratorUninterruptibles.blockUntilConnected(framework);

            verify(framework).blockUntilConnected();

            assertFalse(Thread.currentThread().isInterrupted());
        }

        @Test
        public void blockUntilConnected_NoArgs_Interrupt_Delegates_ThreadInterrupt() throws Exception {
            doThrow(new InterruptedException()).doNothing().when(framework).blockUntilConnected();

            CuratorUninterruptibles.blockUntilConnected(framework);

            verify(framework, atLeastOnce()).blockUntilConnected();

            assertTrue(Thread.currentThread().isInterrupted());
        }

        @Test
        public void blockUntilConnected_TimeProvided_NoInterrupt_Delegates_NoThreadInterrupt() throws Exception {

            when(framework.blockUntilConnected(anyInt(), any(TimeUnit.class))).thenReturn(true);

            assertTrue("Returned value does not match specified return value.",
                    CuratorUninterruptibles.blockUntilConnected(framework, 10, TimeUnit.MILLISECONDS));

            verify(framework).blockUntilConnected(anyInt(), any(TimeUnit.class));

            assertFalse("Thread was interrupted.", Thread.currentThread().isInterrupted());
        }

        @Test
        public void blockUntilConnected_TimeProvided_Interrupt_Delegates_ThreadInterrupt() throws Exception {
            doThrow(new InterruptedException()).doReturn(true).when(framework)
                    .blockUntilConnected(anyInt(), any(TimeUnit.class));

            assertTrue("Returned value does not match specified return value.",
                    CuratorUninterruptibles.blockUntilConnected(framework, 10, TimeUnit.MILLISECONDS));

            verify(framework, atLeastOnce()).blockUntilConnected(anyInt(), any(TimeUnit.class));

            assertTrue("Thread was not interrupted.", Thread.currentThread().isInterrupted());
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class DoubleBarrierTest {
        @Rule
        public final Timeout timeout = new Timeout(10, TimeUnit.SECONDS);

        @Mock
        private DistributedDoubleBarrier doubleBarrier;

        @Test
        public void leave_NoArgs_NoInterrupt_Delegates_NoThreadInterrupt() throws Exception {
            CuratorUninterruptibles.leave(doubleBarrier);

            verify(doubleBarrier).leave();

            assertFalse(Thread.currentThread().isInterrupted());
        }

        @Test
        public void leave_NoArgs_Interrupt_Delegates_ThreadInterrupt() throws Exception {
            doThrow(new InterruptedException()).doNothing().when(doubleBarrier).leave();

            CuratorUninterruptibles.leave(doubleBarrier);

            verify(doubleBarrier, atLeastOnce()).leave();

            assertTrue(Thread.currentThread().isInterrupted());
        }

        @Test
        public void enter_NoArgs_NoInterrupt_Delegates_NoThreadInterrupt() throws Exception {
            CuratorUninterruptibles.enter(doubleBarrier);

            verify(doubleBarrier).enter();

            assertFalse(Thread.currentThread().isInterrupted());
        }

        @Test
        public void enter_NoArgs_Interrupt_Delegates_ThreadInterrupt() throws Exception {

            doThrow(new InterruptedException()).doNothing().when(doubleBarrier).enter();

            CuratorUninterruptibles.enter(doubleBarrier);

            verify(doubleBarrier, atLeastOnce()).enter();

            assertTrue(Thread.currentThread().isInterrupted());
        }

    }
}
