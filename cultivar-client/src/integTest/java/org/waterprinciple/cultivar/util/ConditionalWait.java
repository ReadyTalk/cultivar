package org.waterprinciple.cultivar.util;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Monitor;

public class ConditionalWait {
    private static final long MAX_SLEEP_FOR_PROPAGATION_SECONDS = 5L;

    private final Monitor.Guard guard;

    private final Monitor monitor = new Monitor();

    public ConditionalWait(final Callable<Boolean> criteria) {
        guard = new Monitor.Guard(monitor) {
            @Override
            public boolean isSatisfied() {
                try {
                    return criteria.call();
                } catch (Throwable th) {
                    throw Throwables.propagate(th);
                }
            }
        };
    }

    public void await() throws InterruptedException, TimeoutException {
        monitor.enter();

        try {
            if (!monitor.waitFor(guard, MAX_SLEEP_FOR_PROPAGATION_SECONDS, TimeUnit.SECONDS)) {
                throw new TimeoutException();
            }
        } finally {
            monitor.leave();
        }
    }

    public boolean awaitWithFallthrough() throws InterruptedException {
        try {
            await();
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

}
