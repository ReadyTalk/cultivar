package com.readytalk.cultivar.util;

import java.util.concurrent.TimeUnit;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;

/**
 * Based around Guava's Uninterruptibles framework and uses similar logic.
 * 
 * It should be noted that, for the most part, Curator simply "throws Exception" and will include InterruptedException
 * while doing so. This will subscribe to the same signature as the method in those cases, but will handle the
 * InterruptedException case.
 */
@Beta
public final class CuratorUninterruptibles {

    private CuratorUninterruptibles() {

    }

    public static void blockUntilConnected(final CuratorFramework framework) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    framework.blockUntilConnected();
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @CheckReturnValue
    public static boolean blockUntilConnected(final CuratorFramework framework, final int timeout,
            @Nullable final TimeUnit unit) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    return framework.blockUntilConnected(timeout, unit);
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void leave(final DistributedDoubleBarrier barrier) throws Exception {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    barrier.leave();
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void enter(final DistributedDoubleBarrier barrier) throws Exception {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    barrier.enter();
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
