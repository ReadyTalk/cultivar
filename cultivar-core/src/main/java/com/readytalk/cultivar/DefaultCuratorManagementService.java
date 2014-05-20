package com.readytalk.cultivar;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.AbstractIdleService;

@ThreadSafe
@Beta
public class DefaultCuratorManagementService extends AbstractIdleService implements CuratorManagementService {

    private final CuratorFramework framework;

    private final Set<ConnectionStateListener> connectionStateListeners;
    private final Set<CuratorListener> curatorListeners;
    private final Set<UnhandledErrorListener> unhandledErrorListeners;

    @Inject
    DefaultCuratorManagementService(@Curator final CuratorFramework framework,
            final Set<ConnectionStateListener> connectionStateListeners, final Set<CuratorListener> curatorListeners,
            final Set<UnhandledErrorListener> unhandledErrorListeners) {
        this.framework = framework;
        this.connectionStateListeners = connectionStateListeners;
        this.curatorListeners = curatorListeners;
        this.unhandledErrorListeners = unhandledErrorListeners;
    }

    @Override
    protected void startUp() {

        for (ConnectionStateListener o : connectionStateListeners) {
            addConnectionListener(o);
        }

        for (CuratorListener o : curatorListeners) {
            addCuratorListener(o);
        }

        for (UnhandledErrorListener o : unhandledErrorListeners) {
            addUnhandledErrorListener(o);
        }

        framework.start();
    }

    @Override
    protected void shutDown() {
        framework.close();
    }

    @Override
    public CuratorFramework get() {
        awaitRunning();

        return framework;
    }

    @Override
    public void addConnectionListener(final ConnectionStateListener listener) {
        framework.getConnectionStateListenable().addListener(listener);
    }

    @Override
    public void addUnhandledErrorListener(final UnhandledErrorListener listener) {
        framework.getUnhandledErrorListenable().addListener(listener);
    }

    @Override
    public void addCuratorListener(final CuratorListener listener) {
        framework.getCuratorListenable().addListener(listener);

    }
}
