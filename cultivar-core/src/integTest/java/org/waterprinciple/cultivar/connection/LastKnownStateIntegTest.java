package org.waterprinciple.cultivar.connection;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.waterprinciple.cultivar.CultivarStartStopManager;
import org.waterprinciple.cultivar.Curator;
import org.waterprinciple.cultivar.CuratorManagementService;
import org.waterprinciple.cultivar.CuratorModule;
import org.waterprinciple.cultivar.test.AbstractZookeeperClusterTest;
import org.waterprinciple.cultivar.test.ConditionalWait;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

public class LastKnownStateIntegTest extends AbstractZookeeperClusterTest {

    private final CountDownLatch connectionLatch = new CountDownLatch(1);

    private CultivarStartStopManager manager;

    private LastKnownState state;

    @Before
    public void setUp() throws Exception {
        Injector inj = Guice.createInjector(new CuratorModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(
                        new FixedEnsembleProvider(testingCluster.getConnectString()));
                bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(new ExponentialBackoffRetry(1000, 3));

            }
        }));

        manager = inj.getInstance(CultivarStartStopManager.class);

        CuratorManagementService managementService = inj.getInstance(Key.get(CuratorManagementService.class,
                Curator.class));

        managementService.addConnectionListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
                if (ConnectionState.CONNECTED.equals(newState)) {
                    connectionLatch.countDown();
                }

            }
        });

        state = inj.getInstance(LastKnownState.class);

        manager.startAsync().awaitRunning();
    }

    @After
    public void tearDown() {
        manager.stopAsync().awaitTerminated();
    }

    @Test
    public void lastState_AfterConnection_CONNECTED() throws Exception {
        connectionLatch.await();

        new ConditionalWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ConnectionState.CONNECTED.equals(state.lastState());
            }
        }).await();

        assertEquals(ConnectionState.CONNECTED, state.lastState());
    }
}
