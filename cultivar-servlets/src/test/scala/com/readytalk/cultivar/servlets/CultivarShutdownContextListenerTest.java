package com.readytalk.cultivar.servlets;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.readytalk.cultivar.CultivarStartStopManager;

@RunWith(MockitoJUnitRunner.class)
public class CultivarShutdownContextListenerTest {
    private static final long TIMEOUT_DEFAULT_SECONDS = 1L;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private CultivarStartStopManager manager;

    @Mock
    private ServletContextEvent event;

    private CultivarShutdownContextListener listener;

    @Before
    public void setUp() {
        when(manager.stopAsync()).thenReturn(manager);

        listener = new CultivarShutdownContextListener();
    }

    @After
    public void tearDown() {

        CultivarShutdownContextListener.setCultivarStartStopManager(null, TIMEOUT_DEFAULT_SECONDS, TimeUnit.SECONDS);
    }

    @Test
    public void contextInitialized_ManagerSet_DoesNothing() {
        CultivarShutdownContextListener.setCultivarStartStopManager(manager, TIMEOUT_DEFAULT_SECONDS, TimeUnit.SECONDS);

        verifyZeroInteractions(manager);
    }

    @Test
    public void contextDestroyed_ManagerNotSet_DoesNothing() throws Exception {
        listener.contextDestroyed(event);

        verifyZeroInteractions(event, manager);
    }

    @Test
    public void contextDestroyed_ManagerSet_AwaitTerminatedWithTimeout() throws Exception {
        CultivarShutdownContextListener.setCultivarStartStopManager(manager, TIMEOUT_DEFAULT_SECONDS, TimeUnit.SECONDS);

        listener.contextDestroyed(event);

        verify(manager).awaitTerminated(TIMEOUT_DEFAULT_SECONDS, TimeUnit.SECONDS);
    }

    @Test
    public void contextDestroyed_ManagerSet_StopAsyncThrowsException_DoesNothing() {
        when(manager.stopAsync()).thenThrow(new IllegalStateException("test"));

        CultivarShutdownContextListener.setCultivarStartStopManager(manager, TIMEOUT_DEFAULT_SECONDS, TimeUnit.SECONDS);

        listener.contextDestroyed(event);

        verify(manager).stopAsync();
        verifyNoMoreInteractions(manager);
    }

    @Test
    public void contextDestroyed_ManagerSet_AwaitTerminatedThrowsISE_DoesNothing() throws Exception {
        doThrow(new IllegalStateException("test")).when(manager).awaitTerminated(anyLong(), any(TimeUnit.class));

        CultivarShutdownContextListener.setCultivarStartStopManager(manager, TIMEOUT_DEFAULT_SECONDS, TimeUnit.SECONDS);

        listener.contextDestroyed(event);

        verify(manager).awaitTerminated(anyLong(), any(TimeUnit.class));
    }

    @Test
    public void setCultivarStartStopManager_ZeroTime_ThrowsIAE() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        CultivarShutdownContextListener.setCultivarStartStopManager(null, -1L, TimeUnit.SECONDS);
    }

    @Test
    public void setCultivarStartStopManager_NullTimeUnit_ThrowsNPE() throws Exception {
        thrown.expect(NullPointerException.class);

        CultivarShutdownContextListener.setCultivarStartStopManager(null, 5L, null);
    }
}
