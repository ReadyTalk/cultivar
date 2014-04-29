package org.waterprinciple.cultivar.connection;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.apache.curator.framework.state.ConnectionState;

import com.google.common.annotations.Beta;

/**
 * A tool that listens for state changes in the connection to ZooKeeper and reports them.
 */
@ThreadSafe
@Beta
public class LastKnownState {

    private final AtomicReference<ConnectionState> state = new AtomicReference<ConnectionState>(ConnectionState.LOST);

    @Inject
    LastKnownState() {

    }

    public ConnectionState lastState() {
        return state.get();
    }

    void setState(final ConnectionState newState) {
        state.set(newState);
    }
}
