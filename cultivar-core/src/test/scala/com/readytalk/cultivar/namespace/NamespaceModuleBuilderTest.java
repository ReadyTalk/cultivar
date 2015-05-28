package com.readytalk.cultivar.namespace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.readytalk.cultivar.Cultivar;
import com.readytalk.cultivar.Curator;

@RunWith(Enclosed.class)
public class NamespaceModuleBuilderTest {

    public static class BuilderTest {

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Test
        public void create_GeneratesNotNull() {
            assertNotNull(NamespaceModuleBuilder.create());
        }

        @Test
        public void targetAnnotation_NullClass_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            NamespaceModuleBuilder.create().targetAnnotation((Class<? extends Annotation>) null);
        }

        @Test
        public void targetAnnotation_NullAnnotation_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            NamespaceModuleBuilder.create().targetAnnotation((Annotation) null);
        }

        @Test
        public void targetAnnotation_ValidAnnotation_ReturnsSelf() {
            NamespaceModuleBuilder builder = NamespaceModuleBuilder.create();

            assertEquals(builder, builder.targetAnnotation(Names.named("foo")));
        }

        @Test
        public void targetAnnotation_ValidClass_ReturnsSelf() {
            NamespaceModuleBuilder builder = NamespaceModuleBuilder.create();

            assertEquals(builder, builder.targetAnnotation(Cultivar.class));
        }

        @Test
        public void newNamespace_AnyValue_ReturnsSelf() {
            NamespaceModuleBuilder builder = NamespaceModuleBuilder.create();

            assertEquals(builder, builder.newNamespace("foo"));
        }

        @Test
        public void build_NoNamespace_NoTargetAnnotation_ThrowsISE() {
            thrown.expect(IllegalStateException.class);
            NamespaceModuleBuilder.create().build();
        }

        @Test
        public void build_NamespaceProvided_NoTargetAnnotation_ThrowsISE() {
            thrown.expect(IllegalStateException.class);
            NamespaceModuleBuilder.create().newNamespace("foo").build();
        }

        @Test
        public void build_NoNamespace_TargetAnnotationProvided_ThrowsISE() {
            thrown.expect(IllegalStateException.class);
            NamespaceModuleBuilder.create().targetAnnotation(Cultivar.class).build();
        }

        @Test
        public void build_NamespaceProvided_TargetAnnotationProvided_ReturnsNotNull() {
            assertNotNull(NamespaceModuleBuilder.create().newNamespace("foo").targetAnnotation(Cultivar.class).build());
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class ModuleTest {

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Mock
        private CuratorFramework framework;

        private Module module;

        @Before
        public void setUp() {
            module = NamespaceModuleBuilder.create().newNamespace("foo").targetAnnotation(Cultivar.class).build();
        }

        @Test
        public void createInjector_Module_NoFramework_ThrowsCreationException() {
            thrown.expect(CreationException.class);

            Guice.createInjector(module);
        }

        @Test
        public void createInjector_Module_FrameworkBound_CreatesInjector() {

            assertNotNull(Guice.createInjector(module, new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);
                }
            }));
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class InjectorTest {

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Mock
        private CuratorFramework framework;

        private Injector inj;

        private Injector injNullNamespace;

        @Before
        public void setUp() {
            inj = Guice.createInjector(
                    NamespaceModuleBuilder.create().newNamespace("foo").targetAnnotation(Cultivar.class).build(),
                    new AbstractModule() {
                        @Override
                        protected void configure() {
                            bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);
                        }
                    });

            injNullNamespace = Guice.createInjector(NamespaceModuleBuilder.create().newNamespace(null)
                    .targetAnnotation(Cultivar.class).build(), new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);
                }
            });
        }

        @Test
        public void getExistingBinding_CuratorFramework_IsSingleton() {
            assertTrue(Scopes.isSingleton(inj.getExistingBinding(Key.get(CuratorFramework.class, Cultivar.class))));
        }

        @Test
        public void getInstance_CuratorFramework_HasNamespace() {
            CuratorFramework namespaced = inj.getInstance(Key.get(CuratorFramework.class, Cultivar.class));

            assertTrue("Not a NamespacedCuratorFramework", namespaced instanceof NamespacedCuratorFramework);

            assertEquals("foo", ((NamespacedCuratorFramework) namespaced).getDelegateNamespace());
        }

        @Test
        public void getInstance_NullNamespace_CuratorFramework_HasNullNamespace() {
            CuratorFramework namespaced = injNullNamespace.getInstance(Key.get(CuratorFramework.class, Cultivar.class));

            assertTrue("Not a NamespacedCuratorFramework", namespaced instanceof NamespacedCuratorFramework);

            assertNull(
                    "Namespace is not null: "
                            + String.valueOf(((NamespacedCuratorFramework) namespaced).getDelegateNamespace()),
                    ((NamespacedCuratorFramework) namespaced).getDelegateNamespace());
        }
    }
}
