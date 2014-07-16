package com.readytalk.cultivar.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class HealthCheckModule extends AbstractModule {

    @Override
    protected void configure() {

        Multibinder<HealthCheck> healthchecks = Multibinder.newSetBinder(binder(), HealthCheck.class);

        healthchecks.addBinding().to(ConnectionHealth.class);
        healthchecks.addBinding().to(CuratorManagerStatus.class);
    }
}
