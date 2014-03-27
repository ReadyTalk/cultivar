package org.waterprinciple.cultivar.leader;

import org.apache.curator.framework.recipes.leader.LeaderLatch;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.AbstractExecutionThreadService;

/**
 * An AbstractExecutionThreadService with a built-in isRunning() loop that blocks until it has leadership.
 */
@Beta
public abstract class AbstractLeaderExecutionThreadService extends AbstractExecutionThreadService implements
        LeaderService {

    private final LeaderLatch latch;

    AbstractLeaderExecutionThreadService(final LeaderLatch latch) {
        this.latch = latch;
    }

    @Override
    protected final void run() throws Exception {
        while (isRunning()) {
            runWithLatch();
        }

    }

    @VisibleForTesting
    final void runWithLatch() throws Exception {
        latch.await();

        doRun();
    }

    @Override
    protected final void startUp() throws Exception {
        super.startUp();

        latch.start();

        doStartup();
    }

    @Override
    protected final void shutDown() throws Exception {
        super.shutDown();

        try {
            doShutdown();
        } finally {
            latch.close();
        }

    }

    protected void doStartup() throws Exception {
    }

    protected void doShutdown() throws Exception {
    }

    protected abstract void doRun();
}
