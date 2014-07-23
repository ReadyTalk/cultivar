package com.readytalk.cultivar.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

public class HealthCheckModule extends AbstractModule {

    @Override
    protected void configure() {

        MapBinder<String, HealthCheck> healthchecks = MapBinder.newMapBinder(binder(), String.class, HealthCheck.class);

        healthchecks.addBinding("cultivar.connection").to(ConnectionHealth.class);
        healthchecks.addBinding("cultivar.manager.status").to(CuratorManagerStatus.class);
    }
}
