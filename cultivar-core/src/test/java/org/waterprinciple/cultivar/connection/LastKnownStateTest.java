package org.waterprinciple.cultivar.connection;

import static org.junit.Assert.assertEquals;

import org.apache.curator.framework.state.ConnectionState;
import org.junit.Before;
import org.junit.Test;

public class LastKnownStateTest {

    private LastKnownState state;

    @Before
    public void setUp() throws Exception {
        state = new LastKnownState();
    }

    @Test
    public void lastState_NoneSet_ReturnsLOST() {
        assertEquals(ConnectionState.LOST, state.lastState());
    }

    @Test
    public void setState_ConnectionState_lastStateReturnsValue() {
        state.setState(ConnectionState.READ_ONLY);

        assertEquals(ConnectionState.READ_ONLY, state.lastState());
    }
}
