package com.readytalk.cultivar.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceProviderBuilder;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.readytalk.cultivar.Curator;
import com.readytalk.cultivar.CuratorService;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

@RunWith(Enclosed.class)
public class ServiceDiscoveryModuleBuilderTest {

    @SuppressWarnings("ConstantConditions")
    @RunWith(MockitoJUnitRunner.class)
    public static class BuilderTest {

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Mock
        private ServiceDiscoveryBuilder<Void> builder;

        @Mock
        private InstanceSerializer<Void> serializer;

        private ServiceDiscoveryModuleBuilder<Void> moduleBuilder;

        @Before
        public void setUp() {
            moduleBuilder = ServiceDiscoveryModuleBuilder.create();
        }

        @Test
        public void create_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            ServiceDiscoveryModuleBuilder.create(null);
        }

        @Test
        public void basePath_NotNull_ReturnsSelf() {
            assertEquals(moduleBuilder, moduleBuilder.basePath(""));
        }

        @Test
        public void basePath_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            moduleBuilder.basePath(null);
        }

        @Test
        public void annotation_Annotation_ReturnsSelf() {
            assertEquals(moduleBuilder, moduleBuilder.annotation(Names.named("test")));
        }

        @Test
        public void annotation_Annotation_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            moduleBuilder.annotation((Annotation) null);
        }

        @Test
        public void annotation_Class_ReturnsSelf() {
            assertEquals(moduleBuilder, moduleBuilder.annotation(Curator.class));
        }

        @Test
        public void annotation_Class_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            moduleBuilder.annotation((Class<? extends Annotation>) null);
        }

        @Test
        public void serializer_NotNull_ReturnsSelf() {
            assertEquals(moduleBuilder, moduleBuilder.serializer(serializer));
        }

        @Test
        public void serializer_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            moduleBuilder.serializer(null);
        }

        @Test
        public void build_BasePathNotSet_ThrowsISE() {
            thrown.expect(IllegalStateException.class);

            moduleBuilder.annotation(Curator.class).build();
        }

        @Test
        public void build_AnnotationNotSet_ThrowsISE() {
            thrown.expect(IllegalStateException.class);

            moduleBuilder.basePath("/test").build();
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class ModuleTest {

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Mock
        private ServiceDiscoveryBuilder<Void> builder;

        @Mock
        private CuratorFramework framework;

        private ServiceDiscoveryModuleBuilder<Void> moduleBuilder;

        private Injector inj;

        @Before
        public void setUp() {
            moduleBuilder = ServiceDiscoveryModuleBuilder.create().basePath("/test").annotation(Curator.class);

            inj = Guice.createInjector(moduleBuilder.build(), new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                }
            });
        }

        @Test
        public void createInjector_NoCuratorFramework_ThrowsCreationException() {
            thrown.expect(CreationException.class);

            Guice.createInjector(moduleBuilder.build());
        }

        @Test
        public void getInstance_ServiceDiscoveryManager_Exists() {
            assertNotNull(inj.getExistingBinding(Key.get(new TypeLiteral<ServiceDiscoveryManager<Void>>() {
            }, Curator.class)));
        }

        @Test
        public void getExistingBinding_ServiceDiscovery_Exists() {
            assertNotNull(inj.getExistingBinding(Key.get(new TypeLiteral<ServiceDiscovery<Void>>() {
            }, Curator.class)));
        }

        @Test
        public void getInstance_ServiceDiscoveryManager_IsSingleton() {

            assertTrue("Should be a singleton.",
                    Scopes.isSingleton(inj.getBinding(Key.get(new TypeLiteral<ServiceDiscoveryManager<Void>>() {
                    }, Curator.class))));
        }

        @Test
        public void getInstance_SetOfCuratorServices_IncludesBoundServiceDiscoveryManager() {
            Set<CuratorService> services = inj.getInstance(Key.get(new TypeLiteral<Set<CuratorService>>() {
            }));

            assertTrue(services.contains(inj.getInstance(Key.get(new TypeLiteral<ServiceDiscoveryManager<Void>>() {
            }, Curator.class))));
        }

        @Test
        public void getExistingBinding_ServiceProviderBuilder_Exists() {
            assertNotNull(inj.getExistingBinding(Key.get(new TypeLiteral<ServiceProviderBuilder<Void>>() {
            }, Curator.class)));
        }

        @Test
        public void getBinding_ServiceProviderBuilder_NotSingleton() {
            assertFalse(Scopes.isSingleton(inj.getBinding(Key.get(new TypeLiteral<ServiceProviderBuilder<Void>>() {
            }, Curator.class))));
        }

    }
}
