package com.readytalk.cultivar.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.readytalk.cultivar.CultivarStartStopManager;
import com.readytalk.cultivar.Curator;
import com.readytalk.cultivar.CuratorModule;
import com.readytalk.cultivar.test.AbstractZookeeperClusterTest;
import com.readytalk.cultivar.test.ConditionalWait;
import com.readytalk.cultivar.util.mapping.StringUTF8ByteArrayMapper;

public class NodeContainerIntegTest extends AbstractZookeeperClusterTest {

    private CultivarStartStopManager manager;

    private CuratorFramework framework;

    private NodeContainer<String> container;

    @Before
    public void setUp() throws Exception {
        Injector inj = Guice.createInjector(
                Stage.PRODUCTION,
                new CuratorModule(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(
                                new FixedEnsembleProvider(testingCluster.getConnectString()));
                        bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(
                                new ExponentialBackoffRetry(10, 10));

                    }
                }),
                new AbstractModule() {
                    @Override
                    protected void configure() {

                        bindConstant().annotatedWith(Names.named("Cultivar.Curator.baseNamespace")).to("dev/test");
                    }
                },
                NodeContainerModuleBuilder.create(String.class).annotation(Curator.class)
                        .mapper(StringUTF8ByteArrayMapper.class).path("/dev/test").build());

        manager = inj.getInstance(CultivarStartStopManager.class);

        framework = inj.getInstance(Key.get(CuratorFramework.class, Curator.class));

        container = inj.getInstance(Key.get(new TypeLiteral<NodeContainer<String>>() {
        }, Curator.class));

        manager.startAsync().awaitRunning();
    }

    @After
    public void tearDown() {
        manager.stopAsync().awaitTerminated();
    }

    @Test
    public void get_Uninitialized_Null() {
        assertNull(container.get());
    }

    @Test
    public void getWithDefault_Uninitialized_Value() {
        String value = "test";

        assertEquals(value, container.get(value));
    }

    @Test
    public void createValue_PropagatesToNode() throws Exception {
        String value = "foo";

        framework.create().creatingParentsIfNeeded().forPath("/dev/test", value.getBytes(Charsets.UTF_8));

        new ConditionalWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return container.get() != null;
            }
        }).await();

        assertEquals(value, container.get());
    }

    @Test
    public void createValue_AfterPropagation_ReturnsValueWhenDefaultSpecified() throws Exception {
        String value = "foo";

        framework.create().creatingParentsIfNeeded().forPath("/dev/test", value.getBytes(Charsets.UTF_8));

        new ConditionalWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return container.get() != null;
            }
        }).await();

        assertEquals(value, container.get("notCorrect"));
    }

    @Test
    public void createValue_PropagatesToNodeOnRebuild() throws Exception {
        String value = "foo";

        framework.create().creatingParentsIfNeeded().forPath("/dev/test", value.getBytes(Charsets.UTF_8));

        container.rebuild();

        assertEquals(value, container.get());
    }

    @Test
    public void createValue_TriggersListenerOnPropagate() throws Exception {
        String value = "foo";

        final CountDownLatch latch = new CountDownLatch(1);

        container.addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                latch.countDown();
            }
        });

        framework.create().creatingParentsIfNeeded().forPath("/dev/test", value.getBytes(Charsets.UTF_8));

        latch.await();

        assertEquals(value, container.get());
    }
}
