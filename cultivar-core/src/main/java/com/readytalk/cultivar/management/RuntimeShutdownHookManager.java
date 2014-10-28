package com.readytalk.cultivar.management;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;
import com.google.inject.Inject;

/**
 * Registers a hook against a provided java.lang.Runtime.
 */
@Beta
class RuntimeShutdownHookManager implements ShutdownManager {

    private volatile Runtime runtime;

    @Inject
    RuntimeShutdownHookManager() {
        runtime = Runtime.getRuntime();
    }

    @Inject(optional = true)
    public void setRuntime(final Runtime runtime) {
        this.runtime = checkNotNull(runtime, "Provided runtime cannot be null.");
    }

    @Override
    public void registerHook(final Runnable hook) {
        runtime.addShutdownHook(new Thread(checkNotNull(hook, "Hook cannot be null.")));
    }
}
