package org.waterprinciple.cultivar.connection;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

@ThreadSafe
class LastKnownStateListener implements ConnectionStateListener {

    private final LastKnownState state;

    @Inject
    LastKnownStateListener(final LastKnownState state) {
        this.state = state;
    }

    @Override
    public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
        state.setState(newState);

    }
}
