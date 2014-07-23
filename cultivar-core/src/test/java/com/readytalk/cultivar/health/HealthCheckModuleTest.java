package com.readytalk.cultivar.health;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.readytalk.cultivar.Curator;
import com.readytalk.cultivar.CuratorModule;

public class HealthCheckModuleTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void createInjector_WithoutCuratorFramework_Fails() {
        thrown.expect(CreationException.class);
        Guice.createInjector(new HealthCheckModule());
    }

    @Test
    public void createInjector_WithFullCuratorModule_BindsHealthChecks() {
        assertEquals("Number of health checks not equal to number bound.", 2,
                Guice.createInjector(new HealthCheckModule(), new CuratorModule(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(
                                new FixedEnsembleProvider(""));
                        bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(
                                new ExponentialBackoffRetry(1000, 3));
                    }
                })).getInstance(Key.get(new TypeLiteral<Map<String, HealthCheck>>() {
                })).size());
    }

}
