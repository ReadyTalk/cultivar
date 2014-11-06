package com.readytalk.cultivar.namespace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.name.Names;
import com.readytalk.cultivar.CultivarStartStopManager;
import com.readytalk.cultivar.Curator;
import com.readytalk.cultivar.CuratorModule;
import com.readytalk.cultivar.test.AbstractZookeeperClusterTest;

public class NamespacesIntegTest extends AbstractZookeeperClusterTest {
    private CultivarStartStopManager manager;

    private CuratorFramework namespacedNullFramework;

    private CuratorFramework namespacedFooFramework;

    @Before
    public void setUp() throws Exception {

        Injector inj = Guice.createInjector(Stage.PRODUCTION, new CuratorModule(new AbstractModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named("Cultivar.Curator.baseNamespace")).to("dev/test");

                bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(
                        new FixedEnsembleProvider(testingCluster.getConnectString()));
                bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(new ExponentialBackoffRetry(10, 10));

            }
        }), new AbstractModule() {
            @Override
            protected void configure() {
                Namespaces.bindNamespaces(binder(), "", "foo");
            }
        });

        manager = inj.getInstance(CultivarStartStopManager.class);

        namespacedNullFramework = inj.getInstance(Key.get(CuratorFramework.class, Namespaces.namespace("")));
        namespacedFooFramework = inj.getInstance(Key.get(CuratorFramework.class, Namespaces.namespace("foo")));

        manager.startAsync().awaitRunning();
    }

    @After
    public void tearDown() throws Exception {

        manager.stopAsync().awaitTerminated();
    }

    @Test
    public void namespacedNullFramework_HasNullNamespace() {

        assertNull(namespacedNullFramework.getNamespace());
        assertEquals("/test", namespacedNullFramework.newNamespaceAwareEnsurePath("/test").getPath());
    }

    @Test
    public void namespacedFooFramework_HasFooNamespace() {

        assertEquals("foo", namespacedFooFramework.getNamespace());
        assertEquals("/foo/test", namespacedFooFramework.newNamespaceAwareEnsurePath("/test").getPath());
    }
}
