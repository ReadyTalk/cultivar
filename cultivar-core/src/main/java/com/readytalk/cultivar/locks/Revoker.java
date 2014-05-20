package com.readytalk.cultivar.locks;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.apache.curator.framework.CuratorFramework;
import com.readytalk.cultivar.Curator;

import com.google.common.annotations.Beta;

/**
 * A lock revoker that allows for injection for easier testing.
 */
@Beta
@ThreadSafe
public class Revoker {

    private final CuratorFramework framework;

    @Inject
    Revoker(@Curator final CuratorFramework framework) {
        this.framework = framework;
    }

    public void attemptRevoke(final String lockPath) throws Exception {
        org.apache.curator.framework.recipes.locks.Revoker.attemptRevoke(framework, lockPath);
    }
}
