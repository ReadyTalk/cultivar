package com.readytalk.cultivar.leader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Stage;
import com.google.inject.name.Names;
import com.readytalk.cultivar.CultivarStartStopManager;
import com.readytalk.cultivar.Curator;
import com.readytalk.cultivar.CuratorModule;
import com.readytalk.cultivar.test.AbstractZookeeperClusterTest;

@RunWith(MockitoJUnitRunner.class)
public class LeaderScheduledServiceIntegTest extends AbstractZookeeperClusterTest {

    @Rule
    public final Timeout timeout = new Timeout(2L, TimeUnit.MINUTES);

    private final AtomicLong counter1 = new AtomicLong(0L);
    private final AtomicLong counter2 = new AtomicLong(0L);

    private final CountDownLatch firstRunLatch = new CountDownLatch(1);

    private CultivarStartStopManager manager;

    private CuratorFramework framework;

    @Before
    public void setUp() throws Exception {

        Injector inj = Guice.createInjector(Stage.PRODUCTION, new CuratorModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(
                        new FixedEnsembleProvider(testingCluster.getConnectString()));
                bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(new ExponentialBackoffRetry(10, 10));

            }
        }), new AbstractModule() {
            @Override
            protected void configure() {
                bind(CountDownLatch.class).toInstance(firstRunLatch);

                install(LeaderServiceModuleBuilder
                        .create(Key.get(ScheduledLoggingLeaderService.class, Names.named("service1")))
                        .implementation(ScheduledLoggingLeaderService.class).dependencies(new AbstractModule() {
                            @Override
                            protected void configure() {
                                bind(AtomicLong.class).toInstance(counter1);
                            }
                        }).build());
                install(LeaderServiceModuleBuilder
                        .create(Key.get(ScheduledLoggingLeaderService.class, Names.named("service2")))
                        .implementation(ScheduledLoggingLeaderService.class).dependencies(new AbstractModule() {
                            @Override
                            protected void configure() {
                                bind(AtomicLong.class).toInstance(counter2);
                            }
                        }).build());

            }

            @Provides
            public LeaderLatch latch(@Curator final CuratorFramework framework) {
                return new LeaderLatch(framework, "/path/to/latch");
            }
        });

        manager = inj.getInstance(CultivarStartStopManager.class);

        framework = inj.getInstance(Key.get(CuratorFramework.class, Curator.class));

        manager.startAsync();
    }

    @After
    public void tearDown() throws Exception {

        manager.stopAsync().awaitTerminated();
    }

    private void awaitRunning() throws InterruptedException {
        manager.awaitRunning();
        firstRunLatch.await();
    }

    @Test
    public void leadership_AtLeastOneLeader() throws Exception {
        manager.awaitRunning();

        assertTrue("No leader selected before 10 second timeout: " + framework.getState(),
                firstRunLatch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void leadership_OnlyOneLeaderIncrementing() throws Exception {
        awaitRunning();

        Thread.sleep(10L);

        if (counter1.get() > 0) {
            assertEquals(0, counter2.get());
        } else {
            assertEquals(0, counter1.get());
        }
    }
}
