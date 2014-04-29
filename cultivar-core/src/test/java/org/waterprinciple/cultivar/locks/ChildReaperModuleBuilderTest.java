package org.waterprinciple.cultivar.locks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.ChildReaper;
import org.apache.curator.framework.recipes.locks.Reaper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.waterprinciple.cultivar.Cultivar;
import org.waterprinciple.cultivar.Curator;
import org.waterprinciple.cultivar.CuratorService;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Types;

@RunWith(Enclosed.class)
public class ChildReaperModuleBuilderTest {

    @SuppressWarnings("ConstantConditions")
    public static class BuilderTest {

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        private ChildReaperModuleBuilder builder;

        @Before
        public void setUp() throws Exception {
            builder = ChildReaperModuleBuilder.create();
        }

        @Test
        public void create_NoArg_ReturnsNonNull() {
            assertNotNull(ChildReaperModuleBuilder.create());
        }

        @Test
        public void create_Annotation_ReturnsNonNull() {
            assertNotNull(ChildReaperModuleBuilder.create(Names.named("test")));
        }

        @Test
        public void create_Class_ReturnsNonNull() {
            assertNotNull(ChildReaperModuleBuilder.create(Cultivar.class));
        }

        @Test
        public void create_ClassNull_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            ChildReaperModuleBuilder.create((Class<? extends Annotation>) null);
        }

        @Test
        public void create_AnnotationNull_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            ChildReaperModuleBuilder.create((Annotation) null);
        }

        @Test
        public void lockPath_Valid_ReturnsSelf() {
            assertEquals(builder, builder.lockPath("/valid"));
        }

        @Test
        public void lockPath_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            builder.lockPath(null);
        }

        @Test
        public void reaperMode_Valid_ReturnsSelf() {
            assertEquals(builder, builder.reaperMode(Reaper.Mode.REAP_INDEFINITELY));
        }

        @Test
        public void reaperMode_Null_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            builder.reaperMode(null);
        }

        @Test
        public void build_NoLockPathOrReaperMode_ThrowsISE() {
            thrown.expect(IllegalStateException.class);

            builder.build();
        }

        @Test
        public void build_NoReaperMode_ThrowsISE() {
            thrown.expect(IllegalStateException.class);

            builder.lockPath("/path").build();
        }

        @Test
        public void build_NoLockPath_ThrowsISE() {
            thrown.expect(IllegalStateException.class);

            builder.reaperMode(Reaper.Mode.REAP_INDEFINITELY).build();
        }

        @Test
        public void build_LockAndPathProvided_ReturnsNotNull() {

            assertNotNull(builder.reaperMode(Reaper.Mode.REAP_INDEFINITELY).lockPath("/path").build());
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class ModuleTest {

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Mock
        private ListeningScheduledExecutorService executorService;

        @Mock
        private CuratorFramework framework;

        private ChildReaperModuleBuilder builder;

        @Before
        public void setUp() {
            builder = ChildReaperModuleBuilder.create().lockPath("/path").reaperMode(Reaper.Mode.REAP_INDEFINITELY);
        }

        @Test
        public void createInjector_WithFrameworkAndExecutorProvidedAndNotBound_ThrowsCreationException() {
            thrown.expect(CreationException.class);

            Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                }
            }, builder.exectuor(Key.get(ScheduledExecutorService.class)).build());
        }

        @Test
        public void createInjector_WithFrameworkAndExecutorProvidedAndBound_BindsChildReaper() {

            Injector inj = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(ScheduledExecutorService.class).toInstance(executorService);
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                }
            }, builder.exectuor(Key.get(ScheduledExecutorService.class)).build());

            inj.getInstance(Key.get(ChildReaper.class, Curator.class));
        }

        @Test
        public void createInjector_WithFrameworkAndNoExecutorProvided_BindsChildReaper() {

            Injector inj = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                }
            }, builder.leaderPath("/reaperleader").build());

            inj.getInstance(Key.get(ChildReaper.class, Curator.class));
        }

        @Test
        public void createInjector_WithoutFramework_ThrowsCreationException() {
            thrown.expect(CreationException.class);

            Guice.createInjector(builder.build());
        }

        @Test
        public void createInjector_WithFramework_BindsChildReaperManager() {

            Injector inj = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                }
            }, builder.build());

            inj.getInstance(Key.get(ChildReaperManager.class, Curator.class));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void createInjector_WithFramework_BindsChildReaperManagerToCuratorServices() {

            Injector inj = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                }
            }, builder.build());

            assertTrue(((Set<CuratorService>) inj.getInstance(Key.get(Types.setOf(CuratorService.class)))).contains(inj
                    .getInstance(Key.get(ChildReaperManager.class, Curator.class))));
        }
    }

}
