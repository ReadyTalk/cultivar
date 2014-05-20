package com.readytalk.cultivar.test;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;

/**
 * A wait method for when things are changing outside of the control of the user and need to be monitored. For things
 * inside of the programmer's control–or where a Listener is avaialble–using a Monitor, Condition, or CountDownLatch is
 * strongly preferred.
 */
@Beta
@ThreadSafe
public class ConditionalWait {
    private static final long DEFAULT_AWAIT_POLL_MILLIS = 50L;

    private final Lock lock = new ReentrantLock();

    @GuardedBy("lock")
    private final Callable<Boolean> criteria;

    private final long sleepTimeoutMillis;

    public ConditionalWait(final Callable<Boolean> criteria) {
        this(criteria, DEFAULT_AWAIT_POLL_MILLIS, TimeUnit.MILLISECONDS);
    }

    public ConditionalWait(final Callable<Boolean> criteria, final long sleepTimeout, final TimeUnit unit) {
        this.criteria = criteria;
        this.sleepTimeoutMillis = unit.toMillis(sleepTimeout);
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while (!criteria.call()) {
                Thread.sleep(sleepTimeoutMillis);
            }

        } catch (Exception ex) {
            Throwables.propagateIfInstanceOf(ex, InterruptedException.class);

            throw Throwables.propagate(ex);
        } finally {
            lock.unlock();
        }
    }
}
