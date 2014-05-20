package com.readytalk.cultivar.leader;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.curator.framework.recipes.leader.LeaderLatch;

import com.google.inject.Inject;

public class ScheduledLoggingLeaderService extends AbstractLeaderScheduledService {

    private final AtomicLong counter;
    private final CountDownLatch firstRunLatch;

    @Inject
    ScheduledLoggingLeaderService(final LeaderLatch latch, final CountDownLatch firstRunLatch, final AtomicLong counter) {
        super(latch);

        this.counter = counter;
        this.firstRunLatch = firstRunLatch;
    }

    @Override
    protected void doRunOneIteration() {
        counter.incrementAndGet();
        firstRunLatch.countDown();
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(1L, 1L, TimeUnit.MILLISECONDS);
    }
}
