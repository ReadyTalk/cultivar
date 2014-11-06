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
import com.readytalk.cultivar.Cultivar;
import com.readytalk.cultivar.CultivarStartStopManager;
import com.readytalk.cultivar.Curator;
import com.readytalk.cultivar.CuratorModule;
import com.readytalk.cultivar.test.AbstractZookeeperClusterTest;

public class NamespaceModuleBuilderIntegTest extends AbstractZookeeperClusterTest {
    private CultivarStartStopManager manager;

    private CuratorFramework namespacedNullFramework;

    private CuratorFramework namespacedFooFramework;

    @Before
    public void setUp() throws Exception {

        Injector inj = Guice.createInjector(
                Stage.PRODUCTION,
                new CuratorModule(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bindConstant().annotatedWith(Names.named("Cultivar.Curator.baseNamespace")).to("dev/test");

                        bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(
                                new FixedEnsembleProvider(testingCluster.getConnectString()));
                        bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(
                                new ExponentialBackoffRetry(10, 10));

                    }
                }), NamespaceModuleBuilder.create().newNamespace(null).targetAnnotation(Cultivar.class).build(),
                NamespaceModuleBuilder.create().newNamespace("foo").targetAnnotation(Names.named("foo")).build());

        manager = inj.getInstance(CultivarStartStopManager.class);

        namespacedNullFramework = inj.getInstance(Key.get(CuratorFramework.class, Cultivar.class));
        namespacedFooFramework = inj.getInstance(Key.get(CuratorFramework.class, Names.named("foo")));

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
