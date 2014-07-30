package com.readytalk.cultivar.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.EnsurePath;
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
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import com.readytalk.cultivar.Curator;
import com.readytalk.cultivar.CuratorService;
import com.readytalk.cultivar.util.mapping.StringUTF8ByteArrayMapper;

@RunWith(Enclosed.class)
public class NodeContainerModuleBuilderTest {

    @SuppressWarnings("ConstantConditions")
    public static class CreationTest {
        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Test
        public void create_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            NodeContainerModuleBuilder.create(null);
        }

        @Test
        public void create_NoArguments_IsNotNull() {
            assertNotNull(NodeContainerModuleBuilder.create());
        }

        @Test
        public void create_WithArguments_IsNotNull() {
            assertNotNull(NodeContainerModuleBuilder.create(String.class));
        }
    }

    @SuppressWarnings("ConstantConditions")
    @RunWith(MockitoJUnitRunner.class)
    public static class BuilderTest {

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        private NodeContainerModuleBuilder<String> builder;

        @Before
        public void setUp() throws Exception {
            builder = NodeContainerModuleBuilder.create(String.class);
        }

        @Test
        public void mapper_ReturnsSelf() {
            assertEquals(builder, builder.mapper(StringUTF8ByteArrayMapper.class));
        }

        @Test
        public void mapper_NullValue_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            builder.mapper(null);
        }

        @Test
        public void path_NullValue_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            builder.path(null);
        }

        @Test
        public void path_ReturnsSelf() {

            assertEquals(builder, builder.path("/dev/test"));
        }

        @Test
        public void annotationWithClass_ReturnsSelf() {

            assertEquals(builder, builder.annotation(Curator.class));
        }

        @Test
        public void annotationWithAnnotation_ReturnsSelf() {

            assertEquals(builder, builder.annotation(Names.named("test")));
        }

        @Test
        public void compressed_ReturnsSelf() {

            assertEquals(builder, builder.compressed());
        }

        @Test
        public void build_WithPathAndAnnotationAndNoMapperSet_ThrowsISE() {
            thrown.expect(IllegalStateException.class);

            builder.annotation(Curator.class).path("/dev/test").build();
        }

        @Test
        public void build_WithMapperAndAnnotationAndNoPathSet_ThrowsISE() {
            thrown.expect(IllegalStateException.class);

            builder.mapper(StringUTF8ByteArrayMapper.class).annotation(Names.named("test")).build();
        }

        @Test
        public void build_WithMapperAndPathSetAndNoAnnotation_ThrowsISE() {
            thrown.expect(IllegalStateException.class);

            builder.mapper(StringUTF8ByteArrayMapper.class).path("test").build();
        }

        @Test
        public void build_WithMapperAndPathAndAnnotationClass_NotNull() {
            assertNotNull(builder.mapper(StringUTF8ByteArrayMapper.class).path("/dev/test").annotation(Curator.class)
                    .build());
        }

        @Test
        public void build_WithMapperAndPathAndAnnotation_NotNull() {
            assertNotNull(builder.mapper(StringUTF8ByteArrayMapper.class).path("/dev/test")
                    .annotation(Names.named("test")).build());
        }

        @Test
        public void createInjector_WithoutCuratorFramework_ThrowsCreationException() {
            thrown.expect(CreationException.class);

            Guice.createInjector(builder.mapper(StringUTF8ByteArrayMapper.class).path("/dev/test")
                    .annotation(Names.named("test")).build());
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class InjectorTest {
        @Mock
        private EnsurePath ensurePath;

        @Mock
        private CuratorFramework framework;

        private Injector injector;

        @Before
        public void setUp() {
            when(framework.newNamespaceAwareEnsurePath(anyString())).thenReturn(ensurePath);

            Module module = NodeContainerModuleBuilder.create(String.class).annotation(Curator.class)
                    .mapper(StringUTF8ByteArrayMapper.class).path("dev/test").build();
            injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);
                }
            }, module);
        }

        @Test
        public void getInstance_NodeContainerWithAnnotation_ReturnsNodeContainer() {
            assertNotNull(injector.getInstance(Key.get(Types.newParameterizedType(NodeContainer.class, String.class),
                    Curator.class)));
        }

        @Test
        public void getExistingBinding_NodeContainerWithAnnotation_IsSingleton() {
            assertTrue(Scopes.isSingleton(injector.getExistingBinding(Key.get(
                    Types.newParameterizedType(NodeContainer.class, String.class), Curator.class))));
        }

        @Test
        public void getInstance_SetOfCuratorServices_IncludesNodeContainer() {
            for (CuratorService o : injector.getInstance(Key.get(new TypeLiteral<Set<CuratorService>>() {
            }))) {
                if (o instanceof NodeContainer) {
                    return;
                }
            }

            fail("NodeContainer not in set of CuratorServices");
        }
    }
}
