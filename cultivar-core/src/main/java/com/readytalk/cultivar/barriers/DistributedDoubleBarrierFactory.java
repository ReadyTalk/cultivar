package com.readytalk.cultivar.barriers;

import static com.google.common.base.Preconditions.checkArgument;

import javax.annotation.Nonnegative;
import javax.inject.Inject;

import com.google.common.annotations.Beta;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import com.readytalk.cultivar.Curator;

/**
 * Creates a DistributedDoubleBarrier.
 */
@Beta
public class DistributedDoubleBarrierFactory {
    private final CuratorFramework framework;

    @Inject
    DistributedDoubleBarrierFactory(@Curator final CuratorFramework framework) {
        this.framework = framework;
    }

    public DistributedDoubleBarrier create(final String barrierPath, @Nonnegative final int memberQty) {
        checkArgument(memberQty > 0, "Member quantity must be greater than zero.");

        return new DistributedDoubleBarrier(framework, barrierPath, memberQty);
    }
}
