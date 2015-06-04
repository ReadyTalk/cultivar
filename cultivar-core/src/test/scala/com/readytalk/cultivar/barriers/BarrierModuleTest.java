package com.readytalk.cultivar.barriers;

import static org.junit.Assert.assertNotNull;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.readytalk.cultivar.Curator;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.Stage;

@RunWith(MockitoJUnitRunner.class)
public class BarrierModuleTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private CuratorFramework framework;

    @Test
    public void createInjector_NoFramework_ThrowsCreationException() {
        thrown.expect(CreationException.class);

        Guice.createInjector(Stage.DEVELOPMENT, new BarrierModule());
    }

    @Test
    public void createInjector_WithFramework_BindsDistributedBarrierFactory() {

        assertNotNull("Not bound.", Guice.createInjector(Stage.DEVELOPMENT, new AbstractModule() {
            @Override
            protected void configure() {
                bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

            }
        }, new BarrierModule()).getExistingBinding(Key.get(DistributedBarrierFactory.class)));
    }

    @Test
    public void createInjector_WithFramework_BindsDistributedDoubleBarrierFactory() {

        assertNotNull("Not bound.", Guice.createInjector(Stage.DEVELOPMENT, new AbstractModule() {
            @Override
            protected void configure() {
                bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

            }
        }, new BarrierModule()).getExistingBinding(Key.get(DistributedDoubleBarrierFactory.class)));
    }

}
