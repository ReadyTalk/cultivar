package org.waterprinciple.cultivar.leader;

import org.apache.curator.framework.recipes.leader.LeaderLatch;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.AbstractScheduledService;

/**
 * An AbstractScheduledService that checks for leadership before executing. It will always run on the given schedule,
 * but will only execute if it has leadership.
 */
@Beta
public abstract class AbstractLeaderScheduledService extends AbstractScheduledService implements LeaderService {

    private final LeaderLatch latch;

    AbstractLeaderScheduledService(final LeaderLatch latch) {
        this.latch = latch;
    }

    @Override
    protected final void runOneIteration() throws Exception {
        if (latch.hasLeadership()) {
            doRunOneIteration();
        }
    }

    @Override
    protected final void startUp() throws Exception {
        super.startUp();

        latch.start();

        doStartup();
    }

    @Override
    protected final void shutDown() throws Exception {

        try {
            doShutdown();
        } finally {
            latch.close();
        }

        super.shutDown();
    }

    /**
     * Run when the service is started, after the latch is started.
     * 
     * By default this method does nothing.
     */
    protected void doStartup() throws Exception {
    }

    /**
     * Run when the service is started, before the latch has been closed.
     * 
     * By default this method does nothing.
     */
    protected void doShutdown() throws Exception {
    }

    /**
     * Behaves as AbstractScheduledService#runOneIteration, but first checking for latch ownership.
     */
    protected abstract void doRunOneIteration() throws Exception;
}
