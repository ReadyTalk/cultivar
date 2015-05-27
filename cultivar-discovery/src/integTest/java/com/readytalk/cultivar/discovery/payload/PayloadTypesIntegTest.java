package com.readytalk.cultivar.discovery.payload;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;

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

import com.google.common.collect.ImmutableMap;
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
import com.readytalk.cultivar.discovery.RegistrationModule;
import com.readytalk.cultivar.discovery.RegistrationServiceModuleBuilder;
import com.readytalk.cultivar.discovery.ServiceDiscoveryModuleBuilder;
import com.readytalk.cultivar.discovery.ServiceProviderModuleBuilder;
import com.readytalk.cultivar.test.AbstractZookeeperClusterTest;
import com.readytalk.cultivar.test.ConditionalWait;

public class PayloadTypesIntegTest extends AbstractZookeeperClusterTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final ImmutableProperties props = ImmutableProperties.create(ImmutableMap.of("key", "value"));

    private ServiceProvider<ImmutableProperties> provider;

    private CultivarStartStopManager manager;

    @Before
    public void setUp() throws Exception {

        ServiceInstance<ImmutableProperties> service1 = ServiceInstance.<ImmutableProperties>builder().id("randomid").name("service").payload(props)
                .build();

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
                ServiceDiscoveryModuleBuilder.create(ImmutableProperties.class).annotation(Curator.class)
                        .basePath("/discovery").build(), ServiceProviderModuleBuilder.create(ImmutableProperties.class)
                        .name("service").discovery(Curator.class).annotation(Cultivar.class).build(),
                RegistrationServiceModuleBuilder.create(ImmutableProperties.class).discoveryAnnotation(Curator.class)
                        .targetAnnotation(Curator.class).provider(Providers.of(service1)).build());

        provider = inj.getInstance(Key.get(new TypeLiteral<ServiceProvider<ImmutableProperties>>() {
        }, Cultivar.class));

        manager = inj.getInstance(CultivarStartStopManager.class);

    }

    private void awaitPopulation() throws Exception {
        new ConditionalWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return provider.getAllInstances().size() > 0;
            }
        }).await();
    }

    @After
    public void tearDown() throws Exception {
        manager.stopAsync().awaitTerminated();

    }

    @Test
    public void provider_DeserializesProperties() throws Exception {
        manager.startAsync().awaitRunning();

        awaitPopulation();

        assertEquals(props, provider.getInstance().getPayload());
    }
}
