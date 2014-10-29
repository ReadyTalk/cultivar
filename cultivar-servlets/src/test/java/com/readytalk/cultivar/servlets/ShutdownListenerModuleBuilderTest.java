package com.readytalk.cultivar.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.readytalk.cultivar.CultivarStartStopManager;

@RunWith(Enclosed.class)
public class ShutdownListenerModuleBuilderTest {

    @SuppressWarnings("ConstantConditions")
    public static class BuilderTest {
        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Test
        public void create_NullClass_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            ShutdownListenerModuleBuilder.create(null);
        }

        @Test
        public void shutdownTimeout_ZeroTime_ThrowsIAE() {
            thrown.expect(IllegalArgumentException.class);

            ShutdownListenerModuleBuilder.create().shutdownTimeout(0L, TimeUnit.SECONDS);
        }

        @Test
        public void shutdownTimeout_NullUnit_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            ShutdownListenerModuleBuilder.create().shutdownTimeout(5L, null);
        }

        @Test
        public void extraDependencies_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            ShutdownListenerModuleBuilder.create().extraDependencies(null);
        }

        @Test
        public void build_WithDefaults_ReturnsModule() {
            assertNotNull(ShutdownListenerModuleBuilder.create().build());
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class ModuleTest {

        @Mock
        private CultivarStartStopManager manager;

        @After
        public void tearDown() {
            CultivarShutdownContextListener.setCultivarStartStopManager(null, 1L, TimeUnit.SECONDS);
        }

        @Test
        public void createInjector_WithExtraDependencies_Succeeds() {

            Guice.createInjector(ShutdownListenerModuleBuilder.create().extraDependencies(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CultivarStartStopManager.class).toInstance(manager);
                }
            }).build());

            assertEquals("Manager not statically injected.", manager, CultivarShutdownContextListener.getManager());
        }

        @Test
        public void createInjector_WithDependencies_Succeeds() {

            Guice.createInjector(ShutdownListenerModuleBuilder.create().build(), new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CultivarStartStopManager.class).toInstance(manager);
                }
            });

            assertEquals("Manager not statically injected.", manager, CultivarShutdownContextListener.getManager());
        }
    }
}
