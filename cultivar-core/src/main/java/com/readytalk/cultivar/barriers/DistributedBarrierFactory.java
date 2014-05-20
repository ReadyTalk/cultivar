package com.readytalk.cultivar.barriers;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import com.readytalk.cultivar.Curator;

import com.google.common.annotations.Beta;

/**
 * A simple factory for generating barriers.
 */
@Beta
public class DistributedBarrierFactory {
    private final CuratorFramework framework;

    @Inject
    DistributedBarrierFactory(@Curator final CuratorFramework framework) {
        this.framework = framework;
    }

    public DistributedBarrier create(final String barrierPath) {
        return new DistributedBarrier(framework, checkNotNull(barrierPath));
    }
}
