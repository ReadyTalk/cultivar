package org.waterprinciple.cultivar.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConditionalWaitTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public final Timeout timeout = new Timeout(5000);

    @Mock
    private Callable<Boolean> condition;

    private ConditionalWait conditionalWait;

    @Before
    public void setUp() throws Exception {
        conditionalWait = new ConditionalWait(condition);
    }

    @Test
    public void await_ConditionTrue_CallsConditionOnce() throws Exception {
        when(condition.call()).thenReturn(true);

        conditionalWait.await();

        verify(condition, times(1)).call();
    }

    @Test
    public void await_ConditionChangesToTrue_CallsUntilTrue() throws Exception {
        when(condition.call()).thenReturn(false, false, true);

        conditionalWait.await();

        verify(condition, times(3)).call();
    }

    @Test
    public void await_Interrupted_ThrowsInterruptedException() throws Exception {
        thrown.expect(InterruptedException.class);

        when(condition.call()).thenReturn(false);
        Thread.currentThread().interrupt();

        conditionalWait.await();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void await_ExceptionThrown_RethrowsException() throws Exception {
        thrown.expect(IllegalStateException.class);

        when(condition.call()).thenThrow(IllegalStateException.class);

        conditionalWait.await();
    }
}
