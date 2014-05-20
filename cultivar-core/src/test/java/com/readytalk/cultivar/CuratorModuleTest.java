package com.readytalk.cultivar;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

@RunWith(MockitoJUnitRunner.class)
public class CuratorModuleTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private EnsembleProvider ensembleProvider;

    @Mock
    private RetryPolicy retryPolicy;

    private Module dependencies;

    @Before
    public void setUp() {
        dependencies = new AbstractModule() {
            @Override
            protected void configure() {
                bind(EnsembleProvider.class).annotatedWith(Curator.class).toInstance(ensembleProvider);
                bind(RetryPolicy.class).annotatedWith(Curator.class).toInstance(retryPolicy);

            }
        };
    }

    @Test
    public void configure_WithDependenciesInjected_AllowsCreatingServiceManager() {
        Injector inj = Guice.createInjector(new CuratorModule(dependencies));

        assertNotNull(inj.getInstance(Key.get(ServiceManager.class, Cultivar.class)));
    }

    @Test
    public void configure_WithDependenciesAdjacent_AllowsCreatingServiceManager() {
        Injector inj = Guice.createInjector(dependencies, new CuratorModule());

        assertNotNull(inj.getInstance(Key.get(ServiceManager.class, Cultivar.class)));
    }

    @Test
    public void configure_WithoutCorrectDependencies_ThrowsCreationException() {
        thrown.expect(CreationException.class);

        Guice.createInjector(new CuratorModule());
    }

    @Test
    public void configure_NullDependencies_UsesAdjacentDependencies() {
        Injector inj = Guice.createInjector(dependencies, new CuratorModule(null));

        assertNotNull(inj.getInstance(Key.get(CuratorFramework.class, Curator.class)));
    }

    @Test
    public void configure_DependenciesNotCovered_ThrowsCreationException() {
        thrown.expect(CreationException.class);

        Guice.createInjector(new CuratorModule(null));
    }

    @Test
    public void getInstance_CuratorFrameworkMultipleInstances_Identical() {
        Injector inj = Guice.createInjector(new CuratorModule(dependencies));

        assertTrue("Not identically equal.",
                inj.getInstance(Key.get(CuratorFramework.class, Curator.class)) == inj.getInstance(Key.get(
                        CuratorFramework.class, Curator.class)));
    }
}
