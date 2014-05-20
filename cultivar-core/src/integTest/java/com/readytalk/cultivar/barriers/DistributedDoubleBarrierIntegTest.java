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
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
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
public class DistributedDoubleBarrierIntegTest extends AbstractZookeeperClusterTest {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedDoubleBarrierIntegTest.class);

    private final ListeningScheduledExecutorService executorService = MoreExecutors.listeningDecorator(MoreExecutors
            .getExitingScheduledExecutorService(new ScheduledThreadPoolExecutor(1), 100L, TimeUnit.MILLISECONDS));

    @Mock
    private Logger logger;

    private DistributedDoubleBarrier barrierA1;
    private DistributedDoubleBarrier barrierA2;

    private CultivarStartStopManager manager1;
    private CultivarStartStopManager manager2;

    @Before
    public void setUp() throws Exception {
        LOG.info("*** SERVERS *** {}", testingCluster.getConnectString());

        Injector inj1 = Guice.createInjector(Stage.PRODUCTION, new CuratorModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(
                        new FixedEnsembleProvider(testingCluster.getConnectString()));
                bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(new RetryNTimes(10, 10));

            }
        }));

        Injector inj2 = Guice.createInjector(Stage.PRODUCTION, new CuratorModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(
                        new FixedEnsembleProvider(testingCluster.getConnectString()));
                bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(new RetryNTimes(10, 10));

            }
        }));

        manager1 = inj1.getInstance(CultivarStartStopManager.class);
        manager2 = inj2.getInstance(CultivarStartStopManager.class);

        manager1.startAsync();
        manager2.startAsync();

        manager1.awaitRunning();
        manager2.awaitRunning();

        DistributedDoubleBarrierFactory factory1 = inj1.getInstance(DistributedDoubleBarrierFactory.class);
        DistributedDoubleBarrierFactory factory2 = inj2.getInstance(DistributedDoubleBarrierFactory.class);

        barrierA1 = factory1.create("/test", 2);
        barrierA2 = factory2.create("/test", 2);
    }

    @After
    public void tearDown() {

        manager1.stopAsync().awaitTerminated();
        manager2.stopAsync().awaitTerminated();
    }

    @Test
    public void enter_BlocksUntilAllEnteredBlocksUntilAllLeft_AtLeast100Millis() throws Exception {

        final CountDownLatch enterStartLatch = new CountDownLatch(1);

        final CountDownLatch enterFinishLatch = new CountDownLatch(1);

        final CountDownLatch leaveFinishLatch = new CountDownLatch(1);

        ListenableFuture<Void> enterFuture = executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                enterStartLatch.countDown();

                try {
                    barrierA1.enter();
                } catch (Exception ex) {
                    LOG.warn("Exception waiting on barrier.", ex);
                    throw ex;
                }

                enterFinishLatch.countDown();

                return null;
            }
        });

        enterStartLatch.countDown();

        Thread.sleep(100L);

        assertFalse("Thread finished without entering.", enterFuture.isDone());

        barrierA2.enter();

        ListenableFuture<Void> futureLeave = executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                enterFinishLatch.await();

                try {
                    barrierA1.leave();
                } catch (Exception ex) {
                    LOG.warn("Exception waiting on barrier.", ex);
                    throw ex;
                }

                leaveFinishLatch.countDown();

                return null;
            }
        });

        Thread.sleep(100L);

        if (futureLeave.isDone()) {
            // Throws an exception if one was generated.
            futureLeave.get();
        }

        assertFalse("Thread finished without leaving.", futureLeave.isDone());

        barrierA2.leave();

        leaveFinishLatch.await();
    }
}
