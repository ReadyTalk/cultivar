package com.readytalk.cultivar;

import static org.junit.Assert.assertEquals;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.readytalk.cultivar.test.AbstractZookeeperClusterTest;

public class CuratorModuleIntegTest extends AbstractZookeeperClusterTest {

    private Injector inj;

    @Before
    public void setUp() throws Exception {

        inj = Guice.createInjector(new CuratorModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(
                        new FixedEnsembleProvider(testingCluster.getConnectString()));
                bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(new ExponentialBackoffRetry(1000, 3));

            }
        }));

    }

    @After
    public void tearDown() throws Exception {
        inj.getInstance(CultivarStartStopManager.class).stopAsync().awaitTerminated();
    }

    @Test
    public void getInstance_CuratorFramework_LatentState() {
        CuratorFramework framework = inj.getInstance(Key.get(CuratorFramework.class, Curator.class));

        assertEquals(CuratorFrameworkState.LATENT, framework.getState());
    }

    @Test
    public void serviceManager_Start_StartsFramework() {
        CultivarStartStopManager manager = inj.getInstance(CultivarStartStopManager.class);
        CuratorFramework framework = inj.getInstance(Key.get(CuratorFramework.class, Curator.class));

        manager.startAsync().awaitRunning();

        assertEquals(CuratorFrameworkState.STARTED, framework.getState());
    }

    @Test
    public void serviceManager_NonFunctioningZK_StartsFramework() throws Exception {
        testingCluster.stop();

        CultivarStartStopManager manager = inj.getInstance(CultivarStartStopManager.class);
        CuratorFramework framework = inj.getInstance(Key.get(CuratorFramework.class, Curator.class));

        manager.startAsync().awaitRunning();

        assertEquals(CuratorFrameworkState.STARTED, framework.getState());
    }

}
