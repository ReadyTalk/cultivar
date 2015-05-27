package com.readytalk.cultivar;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.readytalk.cultivar.management.ShutdownManager;

@RunWith(MockitoJUnitRunner.class)
public class ShutdownHookTest {

    @Mock
    private CultivarStartStopManager cultivarManager;

    @Mock
    private ShutdownManager shutdownManager;

    private ShutdownHook hook;

    @Before
    public void setUp() throws Exception {
        when(cultivarManager.stopAsync()).thenReturn(cultivarManager);

        hook = new ShutdownHook(cultivarManager);
    }

    @Test
    public void register_NullManager_DoesNothing() {
        ShutdownHook.register(cultivarManager, null);

        verifyZeroInteractions(cultivarManager);
    }

    @Test
    public void register_RegistersHook() {
        ShutdownHook.register(cultivarManager, shutdownManager);

        verify(shutdownManager).registerHook(any(ShutdownHook.class));
    }

    @Test
    public void run_CleanRun_StopsThenAwaits() {
        hook.run();

        InOrder order = inOrder(cultivarManager);

        order.verify(cultivarManager).stopAsync();
        order.verify(cultivarManager).awaitTerminated();
    }

    @Test
    public void run_RuntimeExceptionInStopAsync_DoesNotThrow() {
        when(cultivarManager.stopAsync()).thenThrow(new RuntimeException("test"));

        hook.run();

        verify(cultivarManager).stopAsync();
    }

    @Test
    public void run_RuntimeExceptionInAwaitTerminated_DoesNotThrow() {
        doThrow(new RuntimeException("test")).when(cultivarManager).awaitTerminated();

        hook.run();

        verify(cultivarManager).awaitTerminated();
    }

}
