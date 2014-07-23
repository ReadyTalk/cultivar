package com.readytalk.cultivar;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.AbstractIdleService;

@ThreadSafe
@Beta
public class DefaultCuratorManagementService extends AbstractIdleService implements CuratorManagementService {
    private static final int MAX_CONNECTION_WAIT_SECONDS = Integer.getInteger("cultivar.connection.wait.seconds", 30);

    private final Logger log;

    private final CuratorFramework framework;

    private final Set<ConnectionStateListener> connectionStateListeners;
    private final Set<CuratorListener> curatorListeners;
    private final Set<UnhandledErrorListener> unhandledErrorListeners;

    @Inject
    DefaultCuratorManagementService(@Curator final CuratorFramework framework,
            final Set<ConnectionStateListener> connectionStateListeners, final Set<CuratorListener> curatorListeners,
            final Set<UnhandledErrorListener> unhandledErrorListeners) {
        this(framework, connectionStateListeners, curatorListeners, unhandledErrorListeners, LoggerFactory
                .getLogger(DefaultCuratorManagementService.class));
    }

    DefaultCuratorManagementService(@Curator final CuratorFramework framework,
            final Set<ConnectionStateListener> connectionStateListeners, final Set<CuratorListener> curatorListeners,
            final Set<UnhandledErrorListener> unhandledErrorListeners, final Logger log) {

        this.framework = framework;
        this.connectionStateListeners = connectionStateListeners;
        this.curatorListeners = curatorListeners;
        this.unhandledErrorListeners = unhandledErrorListeners;
        this.log = log;
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

        final Stopwatch start = Stopwatch.createStarted();

        try {
            if (framework.blockUntilConnected(MAX_CONNECTION_WAIT_SECONDS, TimeUnit.SECONDS)) {
                log.info("Connected after {} milliseconds", start.elapsed(TimeUnit.MILLISECONDS));
            } else {
                log.warn("Failed to connect after {} milliseconds", start.elapsed(TimeUnit.MILLISECONDS));
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted after {} milliseconds", start.elapsed(TimeUnit.MILLISECONDS));
        }
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
