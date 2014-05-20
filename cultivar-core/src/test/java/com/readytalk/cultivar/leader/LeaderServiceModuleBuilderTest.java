package com.readytalk.cultivar.leader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import com.readytalk.cultivar.CuratorService;

import com.google.common.base.Equivalence;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

@SuppressWarnings("ConstantConditions")
@RunWith(Enclosed.class)
public class LeaderServiceModuleBuilderTest {

    @SuppressWarnings("NullArgumentToVariableArgMethod")
    public static class BuilderTest {
        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        private LeaderServiceModuleBuilder<LeaderService> serviceModuleBuilder;

        @Before
        public void setUp() throws Exception {
            serviceModuleBuilder = LeaderServiceModuleBuilder.create(Key.get(LeaderService.class));
        }

        @Test
        public void create_NullKey_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            LeaderServiceModuleBuilder.create(null);
        }

        @Test
        public void create_RealKey_ReturnsBuilder() {
            assertNotNull(serviceModuleBuilder);
        }

        @Test
        public void implementation_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            serviceModuleBuilder.implementation(null);
        }

        @Test
        public void dependencies_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            serviceModuleBuilder.dependencies((Module []) null);
        }

        @Test
        public void dependencies_Empty_ThrowsIAE() {
            thrown.expect(IllegalArgumentException.class);

            serviceModuleBuilder.dependencies();
        }

        @Test
        public void dependencies_DependenciesNotSetYet_ReturnsSelf() {
            assertEquals(serviceModuleBuilder, serviceModuleBuilder.dependencies(new AbstractModule() {
                @Override
                protected void configure() {

                }
            }));
        }

        @Test
        public void dependencies_DependenciesAlreadySet_ThrowsISE() {
            thrown.expect(IllegalStateException.class);

            try {
                serviceModuleBuilder.dependencies(new AbstractModule() {
                    @Override
                    protected void configure() {

                    }
                });
            } catch (IllegalStateException ex) {
                throw new RuntimeException("Exception caught while setting dependencies the first time.", ex);
            }

            serviceModuleBuilder.dependencies(new AbstractModule() {
                @Override
                protected void configure() {

                }
            });
        }

        @Test
        public void build_WithoutImplementation_ThrowsISE() {
            thrown.expect(IllegalStateException.class);

            serviceModuleBuilder.build();
        }

        @Test
        public void build_WithImplementation_ReturnsModule() {
            serviceModuleBuilder.implementation(TypeLiteral.get(BlankTestService.class));

            assertNotNull("Module is null.", serviceModuleBuilder.build());
        }
    }

    public static class ModuleTest {

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        private LeaderServiceModuleBuilder<LeaderService> serviceModuleBuilder;

        @Before
        public void setUp() throws Exception {
            serviceModuleBuilder = LeaderServiceModuleBuilder.create(Key.get(LeaderService.class));

            serviceModuleBuilder.implementation(TypeLiteral.get(BlankTestService.class));
        }

        private Module build() {
            return serviceModuleBuilder.dependencies(new AbstractModule() {
                @Override
                protected void configure() {
                    bindConstant().annotatedWith(Names.named("dependency")).to("value");
                }
            }).build();
        }

        @Test
        public void createInjector_WithDependencies_BindsDependencies() {

            Guice.createInjector(build()).getInstance(LeaderService.class);
        }

        @Test
        public void createInjector_WithDependencies_DoesNotExposeDependencies() {
            thrown.expect(ConfigurationException.class);

            Injector inj;

            try {
                inj = Guice.createInjector(build());
            } catch (ConfigurationException ex) {
                throw new IllegalStateException("Unexpected failure in Injector instantiation.", ex);
            }

            inj.getInstance(Key.get(String.class, Names.named("dependency")));
        }

        @Test
        public void getInstance_ReturnsSingleton() {
            Injector inj = Guice.createInjector(build());

            assertTrue(
                    "Not identical objects.",
                    Equivalence.identity().equivalent(inj.getInstance(LeaderService.class),
                            inj.getInstance(LeaderService.class)));
        }

        @Test
        public void createInjector_BindsToCuratorServiceSet() {
            Injector inj = Guice.createInjector(build());

            try {
                assertTrue(inj.getInstance(Key.get(new TypeLiteral<Set<CuratorService>>() {
                })).contains(inj.getInstance(LeaderService.class)));
            } catch (ConfigurationException ex) {
                throw new IllegalStateException("Set is not being bound.", ex);
            }
        }
    }

}
