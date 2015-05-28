package com.readytalk.cultivar.connection;

import static org.mockito.Mockito.verify;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LastKnownStateListenerTest {

    @Mock
    private CuratorFramework framework;

    @Mock
    private LastKnownState state;

    private LastKnownStateListener listener;

    @Before
    public void setUp() throws Exception {
        listener = new LastKnownStateListener(state);
    }

    @Test
    public void stateChanged_NewState_SetsStateOnLastKnownState() {
        listener.stateChanged(framework, ConnectionState.CONNECTED);

        verify(state).setState(ConnectionState.CONNECTED);
    }
}
