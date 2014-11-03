package com.readytalk.cultivar.servlets;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.readytalk.cultivar.CultivarStartStopManager;
import com.readytalk.cultivar.internal.Private;

/**
 * A default implementation of the ServletContextListener that shuts down Cultivar when the servlet shuts down. If
 * Cultivar has already been shut down or otherwise throws an exception it will simply log the exception and continue.
 * 
 * Will only wait for a certain amount of time to shut down in order to allow the service to continue shutting down in a
 * timely fashion. By default this is 2 minutes, but can be set through the builder.
 */
@ThreadSafe
@Beta
public class CultivarShutdownContextListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(CultivarShutdownContextListener.class);

    private static final Lock LOCK = new ReentrantLock();

    private static final long DEFAULT_STOP_TIME_MILLIS = 15000L;

    @GuardedBy("LOCK")
    private static CultivarStartStopManager startStopManager = null;

    @GuardedBy("LOCK")
    private static long stopTime = DEFAULT_STOP_TIME_MILLIS;

    @GuardedBy("LOCK")
    private static TimeUnit stopUnit = TimeUnit.MILLISECONDS;

    @Override
    public void contextInitialized(final ServletContextEvent _sce) {

    }

    @Override
    public void contextDestroyed(final ServletContextEvent _sce) {

        LOCK.lock();
        try {
            if (startStopManager != null) {

                Stopwatch timer = Stopwatch.createStarted();

                try {
                    startStopManager.stopAsync().awaitTerminated(stopTime, stopUnit);
                } catch (Exception ex) {
                    LOG.warn("Exception while shutting down cultivar.", ex);
                }

                LOG.debug("Shutdown of cultivar completed in {} milliseconds.", timer.elapsed(TimeUnit.MILLISECONDS));
            } else {
                LOG.warn("CultivarShutdownContextListener not initialized.");
            }
        } finally {
            LOCK.unlock();
        }
    }

    @Inject
    @VisibleForTesting
    public static void setCultivarStartStopManager(@Nullable final CultivarStartStopManager manager,
            @Named("Cultivar.private.shutdownTime") @Nonnegative final long time, @Private final TimeUnit unit) {
        checkArgument(time > 0L, "Time must be greater than zero.");

        LOCK.lock();
        try {
            stopTime = time;
            stopUnit = checkNotNull(unit, "TimeUnit cannot be null.");
            startStopManager = manager;
            LOG.debug("CultivarShutdownContextListener has been initialized: {}", manager);
        } finally {
            LOCK.unlock();
        }

    }

    @VisibleForTesting
    static CultivarStartStopManager getManager() {
        LOCK.lock();
        try {
            return startStopManager;
        } finally {
            LOCK.unlock();
        }
    }
}
