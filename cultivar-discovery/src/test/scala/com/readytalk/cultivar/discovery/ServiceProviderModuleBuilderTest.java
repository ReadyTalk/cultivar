package com.readytalk.cultivar.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

import org.apache.curator.x.discovery.DownInstancePolicy;
import org.apache.curator.x.discovery.InstanceFilter;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.ServiceProviderBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
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
import com.google.inject.util.Types;

@RunWith(Enclosed.class)
public class ServiceProviderModuleBuilderTest {

    @SuppressWarnings("ConstantConditions")
    @RunWith(MockitoJUnitRunner.class)
    public static class BuilderTest {

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Mock
        private InstanceFilter<Void> filter;

        @Mock
        private ProviderStrategy<Void> providerStrategy;

        @Mock
        private ThreadFactory threadFactory;

        @Mock
        private DownInstancePolicy downInstancePolicy;

        private ServiceProviderModuleBuilder<Void> builder;

        @Before
        public void setUp() throws Exception {
            builder = ServiceProviderModuleBuilder.create();
        }

        @Test
        public void create_NonNullClass_ReturnsNonNullObject() {
            assertNotNull(ServiceProviderModuleBuilder.create(Void.class));
        }

        @Test
        public void create_NullClass_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            ServiceProviderModuleBuilder.create(null);
        }

        @Test
        public void name_NotNull_ReturnsSelf() {
            assertEquals(builder, builder.name(""));
        }

        @Test
        public void name_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            builder.name(null);
        }

        @Test
        public void discovery_Annotation_NotNull_ReturnsSelf() {
            assertEquals(builder, builder.discovery(Names.named("")));
        }

        @Test
        public void discovery_Annotation_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            builder.discovery((Annotation) null);
        }

        @Test
        public void discovery_Class_NotNull_ReturnsSelf() {
            assertEquals(builder, builder.discovery(Curator.class));
        }

        @Test
        public void discovery_Class_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            builder.discovery((Class<? extends Annotation>) null);
        }

        @Test
        public void annotation_Annotation_NotNull_ReturnsSelf() {
            assertEquals(builder, builder.discovery(Names.named("")));
        }

        @Test
        public void annotation_Annotation_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            builder.discovery((Annotation) null);
        }

        @Test
        public void annotation_Class_NotNull_ReturnsSelf() {
            assertEquals(builder, builder.discovery(Curator.class));
        }

        @Test
        public void annotation_Class_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            builder.discovery((Class<? extends Annotation>) null);
        }

        @Test
        public void additionalFilter_NonNullFilter_ReturnsSelf() {
            assertEquals(builder, builder.additionalFilter(filter));
        }

        @Test
        public void additionalFilter_NullFilter_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            builder.additionalFilter(null);
        }

        @Test
        public void providerStrategy_NonNullStrategy_ReturnsSelf() {
            assertEquals(builder, builder.providerStrategy(providerStrategy));
        }

        @Test
        public void providerStrategy_NullStrategy_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            builder.providerStrategy(null);
        }

        @Test
        public void threadFactory_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            builder.threadFactory(null);
        }

        @Test
        public void threadFactory_NotNull_ReturnsSelf() {
            assertEquals(builder, builder.threadFactory(threadFactory));
        }

        @Test
        public void downInstancePolicy_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            builder.downInstancePolicy(null);
        }

        @Test
        public void downInstancePolicy_NotNullPolicy_ReturnsSelf() {
            assertEquals(builder, builder.downInstancePolicy(downInstancePolicy));
        }

        @Test
        public void build_nameNotSet_ThrowsISE() {
            thrown.expect(IllegalStateException.class);
            thrown.expectMessage("name");

            builder.build();

        }

        @Test
        public void build_discoveryNotSet_ThrowsISE() {
            thrown.expect(IllegalStateException.class);
            thrown.expectMessage("discovery");

            builder.name("").build();
        }

        @Test
        public void build_annotationNotSet_ThrowsISE() {
            thrown.expect(IllegalStateException.class);
            thrown.expectMessage("Target annotation");

            builder.name("").discovery(Curator.class).build();
        }

        @Test
        public void build_aneDiscoveryAnnotationSet_ReturnsModule() {

            assertNotNull(builder.name("").discovery(Curator.class).annotation(Curator.class).build());
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class ModuleCreationTest {

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Mock
        private ServiceProviderBuilder<Void> providerBuilder;

        @Before
        public void setUp() {

        }

        @Test
        public void createInjector_ServiceProviderBuilderNotBound_ThrowsCreationException() {
            thrown.expect(CreationException.class);
            thrown.expectMessage("ServiceProviderBuilder");

            Guice.createInjector(ServiceProviderModuleBuilder.create(Void.class).discovery(Curator.class)
                    .annotation(Curator.class).name("test").build());
        }

        @Test
        @SuppressWarnings("unchecked")
        public void createInjector_ServiceProviderBuilderBound_Succeeds() {

            Guice.createInjector(
                    new AbstractModule() {
                        @Override
                        protected void configure() {
                            bind(
                                    (Key<ServiceProviderBuilder<Void>>) Key.get(
                                            Types.newParameterizedType(ServiceProviderBuilder.class, Void.class),
                                            Curator.class)).toInstance(providerBuilder);

                        }
                    },
                    ServiceProviderModuleBuilder.create(Void.class).discovery(Curator.class).annotation(Curator.class)
                            .name("test").build());
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class ModuleTest {

        @Mock
        private ServiceProvider<Void> provider;

        @Mock
        private ServiceProviderBuilder<Void> providerBuilder;

        @Mock
        private InstanceFilter<Void> instanceFilter1;

        @Mock
        private InstanceFilter<Void> instanceFilter2;

        @Mock
        private ProviderStrategy<Void> providerStrategy;

        @Mock
        private ThreadFactory threadFactory;

        @Mock
        private DownInstancePolicy downInstancePolicy;

        private AbstractModule dependencies;

        private Injector inj;

        @Before
        @SuppressWarnings("unchecked")
        public void setUp() {
            when(providerBuilder.build()).thenReturn(provider);

            dependencies = new AbstractModule() {
                @Override
                protected void configure() {
                    bind(
                            (Key<ServiceProviderBuilder<Void>>) Key.get(
                                    Types.newParameterizedType(ServiceProviderBuilder.class, Void.class), Curator.class))
                            .toInstance(providerBuilder);

                }
            };

            inj = Guice.createInjector(
                    dependencies,
                    ServiceProviderModuleBuilder.create(Void.class).discovery(Curator.class)
                            .additionalFilter(instanceFilter1).additionalFilter(instanceFilter2)
                            .providerStrategy(providerStrategy).threadFactory(threadFactory)
                            .downInstancePolicy(downInstancePolicy).annotation(Curator.class).name("test").build());
        }

        @Test
        public void getExistingBinding_ServiceProvider_NotNull() {
            assertNotNull(inj.getExistingBinding(Key.get(new TypeLiteral<ServiceProvider<Void>>() {
            }, Curator.class)));
        }

        @Test
        public void getBinding_ServiceProvider_IsSingleton() {
            assertTrue(Scopes.isSingleton(inj.getBinding(Key.get(new TypeLiteral<ServiceProvider<Void>>() {
            }, Curator.class))));
        }

        @Test
        public void getInstance_WithTwoInstanceFilters_AppliesToProviderBuilderInOrder() {
            inj.getInstance(Key.get(new TypeLiteral<ServiceProvider<Void>>() {
            }, Curator.class));

            InOrder order = inOrder(providerBuilder);

            order.verify(providerBuilder).additionalFilter(instanceFilter1);
            order.verify(providerBuilder).additionalFilter(instanceFilter2);
        }

        @Test
        public void getInstance_WithProviderStrategy_AppliesToProviderBuilder() {
            inj.getInstance(Key.get(new TypeLiteral<ServiceProvider<Void>>() {
            }, Curator.class));

            verify(providerBuilder).providerStrategy(providerStrategy);
        }

        @Test
        public void getInstance_WithDownInstancePolicy_AppliesToProviderBuilder() {
            inj.getInstance(Key.get(new TypeLiteral<ServiceProvider<Void>>() {
            }, Curator.class));

            verify(providerBuilder).downInstancePolicy(downInstancePolicy);
        }

        @Test
        public void createInstance_WithThreadFactory_AppliesToProviderBuilder() {
            inj.getInstance(Key.get(new TypeLiteral<ServiceProvider<Void>>() {
            }, Curator.class));

            verify(providerBuilder).threadFactory(threadFactory);
        }

        @Test
        public void getBinding_ServiceProviderManager_IsSingleton() {
            assertTrue(Scopes.isSingleton(inj.getBinding(Key.get(new TypeLiteral<ServiceProviderManager<Void>>() {
            }, Curator.class))));
        }

        @Test
        public void getInstance_SetOfCuratorServices_IncludesBoundServiceProviderManager() {
            Set<CuratorService> services = inj.getInstance(Key.get(new TypeLiteral<Set<CuratorService>>() {
            }));

            assertTrue(services.contains(inj.getInstance(Key.get(new TypeLiteral<ServiceProviderManager<Void>>() {
            }, Curator.class))));
        }
    }

}
