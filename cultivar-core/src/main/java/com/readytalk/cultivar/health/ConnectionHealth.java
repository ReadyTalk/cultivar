package com.readytalk.cultivar.health;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.data.Stat;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.readytalk.cultivar.Curator;

/**
 * Pings ZK by checking to ensure that the base namespace path exists. Will return healthy if Curator is not started or
 * if it has been deliberately stopped.
 */
@Singleton
public class ConnectionHealth extends HealthCheck {

    private static final TimeUnit REPORTING_TIME_UNIT = TimeUnit.NANOSECONDS;

    private final CuratorFramework framework;

    @Inject
    ConnectionHealth(@Curator final CuratorFramework framework) {
        this.framework = framework;
    }

    @Override
    protected Result check() throws Exception {

        CuratorFrameworkState frameworkState = framework.getState();

        if (CuratorFrameworkState.LATENT.equals(frameworkState) || CuratorFrameworkState.STOPPED.equals(frameworkState)) {
            return Result.healthy("Framework is in %s state", frameworkState);
        }

        Stopwatch timer = Stopwatch.createStarted();

        Stat result = framework.usingNamespace(null).checkExists().forPath("/");

        timer.stop();

        Result retval;

        if (result != null) {
            retval = Result.healthy("Root namespace %s/ found. Result took %s nanoseconds",
                    String.valueOf(framework.getNamespace()), timer.elapsed(REPORTING_TIME_UNIT));
        } else {
            retval = Result.unhealthy("Root namespace %s/ not found! Result took %s nanoseconds",
                    String.valueOf(framework.getNamespace()), timer.elapsed(REPORTING_TIME_UNIT));
        }

        return retval;
    }
}
