package org.waterprinciple.cultivar.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.waterprinciple.cultivar.Cultivar;
import org.waterprinciple.cultivar.CultivarStartStopManager;
import org.waterprinciple.cultivar.Curator;
import org.waterprinciple.cultivar.CuratorModule;
import org.waterprinciple.cultivar.test.AbstractZookeeperClusterTest;
import org.waterprinciple.cultivar.test.ConditionalWait;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class DiscoveryIntegTest extends AbstractZookeeperClusterTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private Injector inj;

    private ServiceDiscovery<Void> discovery;

    private ServiceInstance<Void> service1;

    private ServiceInstance<Void> service2;

    private ServiceProvider<Void> provider;

    private CultivarStartStopManager manager;

    @Before
    public void setUp() throws Exception {

        inj = Guice.createInjector(
                new CuratorModule(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(
                                new FixedEnsembleProvider(testingCluster.getConnectString()));
                        bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(
                                new ExponentialBackoffRetry(1000, 3));

                    }
                }),
                ServiceDiscoveryModuleBuilder.create().annotation(Curator.class).basePath("/discovery").build(),
                ServiceProviderModuleBuilder.create(Void.class).name("service").discovery(Curator.class)
                        .annotation(Cultivar.class).build());

        discovery = inj.getInstance(Key.get(new TypeLiteral<ServiceDiscovery<Void>>() {
        }, Curator.class));

        service1 = ServiceInstance.<Void> builder().name("service").build();

        service2 = ServiceInstance.<Void> builder().name("service").build();

        provider = inj.getInstance(Key.get(new TypeLiteral<ServiceProvider<Void>>() {
        }, Cultivar.class));

        manager = inj.getInstance(CultivarStartStopManager.class);

    }

    @After
    public void tearDown() throws Exception {
        inj.getInstance(CultivarStartStopManager.class).stopAsync().awaitTerminated();
    }

    @Test
    public void register_BeforeStarting_ThrowsISE() throws Exception {
        thrown.expect(IllegalStateException.class);

        discovery.registerService(service1);
    }

    @Test
    public void provider_getAllInstances_BeforeStartingDiscovery_ReturnsNoInstances() throws Exception {
        CuratorFramework framework = inj.getInstance(Key.get(CuratorFramework.class, Curator.class));

        framework.start();
        try {
            assertEquals(0, provider.getAllInstances().size());
        } finally {
            framework.close();
        }
    }

    @Test
    public void discoveryService_AddingOneInstance_ReturnsSingleInstanceOnProvider() throws Exception {
        manager.startAsync().awaitRunning();

        discovery.registerService(service1);

        new ConditionalWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !provider.getAllInstances().isEmpty();
            }
        }).await();

        assertEquals("Instance count does not match.", 1, provider.getAllInstances().size());
        assertEquals("Instance is not equal to service.", service1, provider.getInstance());
    }

    @Test
    public void discoveryService_AddingTwoInstances_ReturnsBothInstanceOnProvider() throws Exception {
        manager.startAsync().awaitRunning();

        discovery.registerService(service1);
        discovery.registerService(service2);

        new ConditionalWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return provider.getAllInstances().size() > 1;
            }
        }).await();

        assertEquals("Instance count does not match.", 2, provider.getAllInstances().size());
        assertTrue("Instances are not equal to services.",
                provider.getAllInstances().containsAll(ImmutableSet.of(service1, service2)));

    }
}
