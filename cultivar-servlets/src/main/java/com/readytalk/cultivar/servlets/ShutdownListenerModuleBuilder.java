package com.readytalk.cultivar.servlets;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.NotThreadSafe;
import javax.servlet.ServletContextListener;

import com.google.common.annotations.Beta;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.readytalk.cultivar.internal.Private;

/**
 * Constructs a module that will statically inject a ServletContextListener.
 */
@Beta
@NotThreadSafe
public class ShutdownListenerModuleBuilder {

    private final Class<? extends ServletContextListener> clazz;

    private long shutdownTime = 2;

    private TimeUnit shutdownUnit = TimeUnit.MINUTES;

    private Module dependencies = new AbstractModule() {
        @Override
        protected void configure() {

        }
    };

    ShutdownListenerModuleBuilder(final Class<? extends ServletContextListener> clazz) {
        this.clazz = clazz;
    }

    /**
     * Creates a ShutdownListenerModuleBuilder for a given ServletContextListener.
     */
    public static ShutdownListenerModuleBuilder create(final Class<? extends ServletContextListener> clazz) {
        return new ShutdownListenerModuleBuilder(checkNotNull(clazz, "Listener class must not be null."));
    }

    /**
     * Creates a ShutdownListenerModuleBuilder using the default CultivarShutdownContextListener.
     */
    public static ShutdownListenerModuleBuilder create() {
        return create(CultivarShutdownContextListener.class);
    }

    /**
     * The maximum amount of time to wait for cultivar. Defaults to 2 minutes.
     * 
     * @param time
     *            The amount of time to wait, must be greater than zero.
     * @param unit
     *            The unit for the amount of time to wait.
     */
    public ShutdownListenerModuleBuilder shutdownTimeout(@Nonnegative final long time, final TimeUnit unit) {
        checkArgument(time > 0, "Time must be greater than zero.");

        this.shutdownTime = time;
        this.shutdownUnit = checkNotNull(unit, "Unit must not be null.");

        return this;
    }

    /**
     * For custom listeners this provides the ability to add extra dependencies.
     */
    public ShutdownListenerModuleBuilder extraDependencies(final Module... modules) {
        this.dependencies = Modules.combine(modules);

        return this;
    }

    /**
     * Build the Module. Will bind the name to the named annotation Cultivar.private.shutdownTime and the time unit to
     * Private and request static injection. Private module so any provided dependencies will remain contained.
     */
    public Module build() {
        return new PrivateModule() {
            @Override
            protected void configure() {
                install(dependencies);

                bindConstant().annotatedWith(Names.named("Cultivar.private.shutdownTime")).to(shutdownTime);

                bind(TimeUnit.class).annotatedWith(Private.class).toInstance(shutdownUnit);

                requestStaticInjection(clazz);
            }
        };
    }
}
