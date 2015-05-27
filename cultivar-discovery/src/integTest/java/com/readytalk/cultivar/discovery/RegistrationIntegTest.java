package com.readytalk.cultivar.discovery;

import static org.junit.Assert.assertEquals;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;
import com.readytalk.cultivar.Cultivar;
import com.readytalk.cultivar.CultivarStartStopManager;
import com.readytalk.cultivar.Curator;
import com.readytalk.cultivar.CuratorModule;
import com.readytalk.cultivar.test.AbstractZookeeperClusterTest;
import com.readytalk.cultivar.test.ConditionalWait;

public class RegistrationIntegTest extends AbstractZookeeperClusterTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private ServiceProvider<Void> provider;

    private CultivarStartStopManager manager;

    @Before
    public void setUp() throws Exception {

        ServiceInstance<Void> service1 = ServiceInstance.<Void>builder().id(UUID.randomUUID().toString()).name("service").build();

        ServiceInstance<Void> service2 = ServiceInstance.<Void>builder().id(UUID.randomUUID().toString()).name("service").build();

        Injector inj = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bindConstant().annotatedWith(Names.named("Cultivar.Curator.baseNamespace")).to("dev/test");
                    }
                },
                new CuratorModule(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(
                                new FixedEnsembleProvider(testingCluster.getConnectString()));
                        bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(
                                new ExponentialBackoffRetry(1000, 3));

                    }
                }),
                new RegistrationModule(),
                ServiceDiscoveryModuleBuilder.create().annotation(Curator.class).basePath("/discovery").build(),
                ServiceProviderModuleBuilder.create(Void.class).name("service").discovery(Curator.class)
                        .annotation(Cultivar.class).build(),
                RegistrationServiceModuleBuilder.create().discoveryAnnotation(Curator.class)
                        .targetAnnotation(Curator.class).provider(Providers.of(service1)).build(),
                RegistrationServiceModuleBuilder
                        .create()
                        .discoveryAnnotation(Curator.class)
                        .targetAnnotation(Cultivar.class)
                        .provider(Providers.of(service2))
                        .updating(
                                10,
                                TimeUnit.SECONDS,
                                MoreExecutors.getExitingScheduledExecutorService(new ScheduledThreadPoolExecutor(1),
                                        10, TimeUnit.MILLISECONDS)).build());

        inj.getInstance(Key.get(ServiceManager.class, Discovery.class));

        provider = inj.getInstance(Key.get(new TypeLiteral<ServiceProvider<Void>>() {
        }, Cultivar.class));

        manager = inj.getInstance(CultivarStartStopManager.class);

    }

    private void awaitPopulation() throws Exception {
        new ConditionalWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return provider.getAllInstances().size() > 1;
            }
        }).await();
    }

    @After
    public void tearDown() throws Exception {
        manager.stopAsync().awaitTerminated();

    }

    @Test
    public void provider_bothInstancesPopulated() throws Exception {
        manager.startAsync().awaitRunning();

        awaitPopulation();

        assertEquals(2, provider.getAllInstances().size());
    }
}
