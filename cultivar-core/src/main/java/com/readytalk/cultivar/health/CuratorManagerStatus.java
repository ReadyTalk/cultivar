package com.readytalk.cultivar.health;

import static com.google.common.util.concurrent.Service.State.FAILED;
import static com.google.common.util.concurrent.Service.State.STOPPING;
import static com.google.common.util.concurrent.Service.State.TERMINATED;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.util.concurrent.Service.State;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.readytalk.cultivar.CultivarStartStopManager;

/**
 * Checks to determine if the connection to ZK is either NEW, STARTING, or has started properly. Fails if the
 * CultivarStartStopManager is in the states FAILED, STOPPING, or TERMINATED.
 */
@Singleton
public class CuratorManagerStatus extends HealthCheck {

    private final CultivarStartStopManager startStopManager;

    @Inject
    CuratorManagerStatus(final CultivarStartStopManager startStopManager) {
        this.startStopManager = startStopManager;
    }

    @Override
    protected Result check() throws Exception {

        State state = startStopManager.state();

        Result retval;

        if (FAILED.equals(state)) {
            retval = Result.unhealthy(startStopManager.failureCause());
        } else if (TERMINATED.equals(state) || STOPPING.equals(state)) {
            retval = HealthCheck.Result.unhealthy("State is %s!", state);
        } else {
            retval = HealthCheck.Result.healthy("Current state is: %s", String.valueOf(state));
        }

        return retval;
    }
}
