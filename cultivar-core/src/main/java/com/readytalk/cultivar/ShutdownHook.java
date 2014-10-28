package com.readytalk.cultivar;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.readytalk.cultivar.management.ShutdownManager;

/**
 * Convenience tools for registering a shutdown hook.
 */
@Beta
@ThreadSafe
class ShutdownHook implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ShutdownHook.class);

    private final CultivarStartStopManager delegate;

    @VisibleForTesting
    ShutdownHook(final CultivarStartStopManager delegate) {
        this.delegate = delegate;
    }

    public static void register(final CultivarStartStopManager cultivarManager,
            @Nullable final ShutdownManager shutdownManager) {

        if (shutdownManager == null) {
            return;
        }

        shutdownManager.registerHook(new ShutdownHook(cultivarManager));

    }

    @Override
    public void run() {
        try {
            delegate.stopAsync().awaitTerminated();
        } catch (RuntimeException ex) {
            LOG.warn("Exception while shutting down cultivar mangager: {}", delegate, ex);
        }
    }
}
