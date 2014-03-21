package org.waterprinciple.cultivar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCuratorManagementServiceTest {

    @Mock
    private CuratorFramework framework;

    @Mock
    private Listenable<ConnectionStateListener> connectionStateListeners;

    @Mock
    private Listenable<UnhandledErrorListener> unhandledErrorListeners;

    @Mock
    private Listenable<CuratorListener> curatorListeners;

    @Mock
    private ConnectionStateListener connectionStateListener;

    @Mock
    private UnhandledErrorListener unhandledErrorListener;

    @Mock
    private CuratorListener curatorListener;

    private DefaultCuratorManagementService service;

    @Before
    public void setUp() throws Exception {

        service = new DefaultCuratorManagementService(framework, ImmutableSet.of(connectionStateListener),
                ImmutableSet.of(curatorListener), ImmutableSet.of(unhandledErrorListener));

        when(framework.getConnectionStateListenable()).thenReturn(connectionStateListeners);

        when(framework.getUnhandledErrorListenable()).thenReturn(unhandledErrorListeners);

        when(framework.getCuratorListenable()).thenReturn(curatorListeners);

    }

    @Test
    public void startUp_StartsFramework() {
        service.startUp();

        verify(framework).start();
    }

    @Test
    public void startUp_WithConnectionStateListeners_AddsListenersBeforeStart() {
        service.startUp();

        InOrder order = inOrder(connectionStateListeners, framework);

        order.verify(connectionStateListeners).addListener(connectionStateListener);
        order.verify(framework).start();

    }

    @Test
    public void startUp_WithCuratorListeners_AddsListenersBeforeStart() {
        service.startUp();

        InOrder order = inOrder(curatorListeners, framework);

        order.verify(curatorListeners).addListener(curatorListener);
        order.verify(framework).start();
    }

    @Test
    public void startUp_WithUnhandledErrorListeners_AddsListenersBeforeStart() {
        service.startUp();

        InOrder order = inOrder(unhandledErrorListeners, framework);

        order.verify(unhandledErrorListeners).addListener(unhandledErrorListener);
        order.verify(framework).start();
    }

    @Test
    public void shutDown_ClosesFramework() {
        service.shutDown();

        verify(framework).close();
    }

    @Test
    public void get_AfterStart_RetunsValue() {
        service.startAsync().awaitRunning();

        assertEquals(framework, service.get());
    }

    @Test
    public void addConnectionListener_WithListener_AddsToDelegate() {
        service.addConnectionListener(connectionStateListener);

        verify(connectionStateListeners).addListener(connectionStateListener);
    }

    @Test
    public void addCuratorListener_WithListener_AddsToDelegate() {
        service.addCuratorListener(curatorListener);

        verify(curatorListeners).addListener(curatorListener);
    }

    @Test
    public void addUnhandledErrorListener_WithListener_AddsToDelegate() {
        service.addUnhandledErrorListener(unhandledErrorListener);

        verify(unhandledErrorListeners).addListener(unhandledErrorListener);
    }
}
