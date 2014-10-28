package com.readytalk.cultivar.management;

import com.google.common.annotations.Beta;

/**
 * Interface to abstract how to register a shutdown hook.
 */
@Beta
public interface ShutdownManager {
    void registerHook(final Runnable hook);
}
