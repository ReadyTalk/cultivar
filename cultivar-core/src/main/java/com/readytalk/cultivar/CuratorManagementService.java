package com.readytalk.cultivar;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.google.common.annotations.Beta;

/**
 * Managers the Curator instance, with convenience methods for adding listeners and starting/stopping the Curator
 * instance.
 */
@Beta
public interface CuratorManagementService extends CuratorService {
    /**
     * Adds a connection listener to Curator.
     */
    void addConnectionListener(ConnectionStateListener listener);

    /**
     * Adds an unhandled error listener to Curator.
     */
    void addUnhandledErrorListener(UnhandledErrorListener listener);

    /**
     * Adds a curator listener to Curator.
     */
    void addCuratorListener(CuratorListener listener);

    /**
     * Gets the curator instance, can block until it is fully initialized.
     */
    CuratorFramework get();
}
