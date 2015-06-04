package com.readytalk.cultivar.barriers;

import static org.junit.Assert.assertFalse;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.readytalk.cultivar.CultivarStartStopManager;
import com.readytalk.cultivar.CuratorModule;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.RetryNTimes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.readytalk.cultivar.Curator;
import com.readytalk.cultivar.test.AbstractZookeeperClusterTest;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

@RunWith(MockitoJUnitRunner.class)
public class DistributedBarrierIntegTest extends AbstractZookeeperClusterTest {

    private static final Logger LOG = LoggerFactory.getLogger(DistributedBarrierIntegTest.class);

    private final ListeningScheduledExecutorService executorService = MoreExecutors.listeningDecorator(MoreExecutors
            .getExitingScheduledExecutorService(new ScheduledThreadPoolExecutor(2), 100L, TimeUnit.MILLISECONDS));

    @Mock
    private Logger logger;

    private DistributedBarrier barrierA1;
    private DistributedBarrier barrierA2;

    private CultivarStartStopManager manager1;
    private CultivarStartStopManager manager2;

    @Before
    public void setUp() throws Exception {

        Injector inj1 = Guice.createInjector(Stage.PRODUCTION, new CuratorModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(
                        new FixedEnsembleProvider(testingCluster.getConnectString()));
                bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(new RetryNTimes(10, 100));

            }
        }));

        Injector inj2 = Guice.createInjector(Stage.PRODUCTION, new CuratorModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(
                        new FixedEnsembleProvider(testingCluster.getConnectString()));
                bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(new RetryNTimes(10, 100));

            }
        }));

        manager1 = inj1.getInstance(CultivarStartStopManager.class);
        manager2 = inj2.getInstance(CultivarStartStopManager.class);

        manager1.startAsync();
        manager2.startAsync();

        manager1.awaitRunning();
        manager2.awaitRunning();

        DistributedBarrierFactory factory1 = inj1.getInstance(DistributedBarrierFactory.class);
        DistributedBarrierFactory factory2 = inj2.getInstance(DistributedBarrierFactory.class);

        barrierA1 = factory1.create("/test");
        barrierA2 = factory2.create("/test");
    }

    @After
    public void tearDown() {
        manager1.stopAsync().awaitTerminated();
        manager2.stopAsync().awaitTerminated();
    }

    @Test
    public void waitOnBarrier_BlocksUntilRemoved_AtLeast100Millis() throws Exception {

        barrierA2.setBarrier();

        final CountDownLatch startLatch = new CountDownLatch(1);

        final CountDownLatch finishLatch = new CountDownLatch(1);

        ListenableFuture<Void> future = executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                startLatch.countDown();

                try {
                    barrierA1.waitOnBarrier();
                } catch (Exception ex) {
                    LOG.warn("Exception waiting on barrier.", ex);
                    throw ex;
                }

                finishLatch.countDown();

                return null;
            }
        });

        startLatch.countDown();

        Thread.sleep(100L);

        assertFalse("Thread finished early.", future.isDone());

        barrierA2.removeBarrier();

        finishLatch.await();
    }

}
