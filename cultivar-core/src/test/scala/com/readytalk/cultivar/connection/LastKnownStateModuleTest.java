package com.readytalk.cultivar.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.util.Types;

@RunWith(MockitoJUnitRunner.class)
public class LastKnownStateModuleTest {

    @Mock
    private CuratorFramework framework;

    private Injector inj;

    @Before
    public void setUp() {
        inj = Guice.createInjector(new LastKnownStateModule());
    }

    @Test
    public void getBinding_LastKnownState_IsSingleton() {

        Binding<LastKnownState> state = inj.getExistingBinding(Key.get(LastKnownState.class));

        assertNotNull("Not bound.", state);

        Scopes.isSingleton(state);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getInstance_SetOfConnectionStateListeners_ContainsLastKnownStateListener() {

        Set<ConnectionStateListener> listeners = (Set<ConnectionStateListener>) inj.getInstance(Key.get(Types
                .setOf(ConnectionStateListener.class)));

        assertTrue(listeners.contains(inj.getInstance(LastKnownStateListener.class)));

    }

    @Test
    public void setState_OnLastKnownStateListener_UpdatesLastKnownState() {

        LastKnownStateListener listener = inj.getInstance(LastKnownStateListener.class);
        LastKnownState state = inj.getInstance(LastKnownState.class);

        listener.stateChanged(framework, ConnectionState.READ_ONLY);

        assertEquals(ConnectionState.READ_ONLY, state.lastState());

    }

}
