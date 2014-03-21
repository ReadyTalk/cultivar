package org.waterprinciple.cultivar;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;

/**
 * A listener that logs changes to the connection state.
 */
@Singleton
@ThreadSafe
@Beta
public class ConnectionStateLogger implements ConnectionStateListener {
    private final Logger logger;

    @Inject
    ConnectionStateLogger() {
        this(LoggerFactory.getLogger(ConnectionStateLogger.class));
    }

    @VisibleForTesting
    ConnectionStateLogger(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void stateChanged(final CuratorFramework _client, final ConnectionState newState) {
        logger.info("State changed detected {}", newState);
    }
}
