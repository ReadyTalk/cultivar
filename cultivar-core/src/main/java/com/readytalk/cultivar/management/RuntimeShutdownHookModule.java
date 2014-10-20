package com.readytalk.cultivar.management;

import com.google.common.annotations.Beta;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Binds a RuntimeShutdownHookManager to the ShutdownManager class that registers hooks against a provided runtime.
 *
 * Optional Bindings:
 * <ul>
 *     <li>: java.lang.Runtime (default: Runtime.getRuntime())</li>
 * </ul>
 */
@Beta
public class RuntimeShutdownHookModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ShutdownManager.class).to(RuntimeShutdownHookManager.class).in(Scopes.SINGLETON);
    }
}
