package org.waterprinciple.cultivar.locks;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.curator.framework.CuratorFramework;
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
public class ReaperModuleBuilderTest {

    @SuppressWarnings("ConstantConditions")
    public static class BuilderTest {

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        private ReaperModuleBuilder builder;

        @Before
        public void setUp() throws Exception {
            builder = ReaperModuleBuilder.create();
        }

        @Test
        public void create_NoArg_ReturnsNonNull() {
            assertNotNull(ReaperModuleBuilder.create());
        }

        @Test
        public void create_Annotation_ReturnsNonNull() {
            assertNotNull(ReaperModuleBuilder.create(Names.named("test")));
        }

        @Test
        public void create_Class_ReturnsNonNull() {
            assertNotNull(ReaperModuleBuilder.create(Cultivar.class));
        }

        @Test
        public void create_ClassNull_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            ReaperModuleBuilder.create((Class<? extends Annotation>) null);
        }

        @Test
        public void create_AnnotationNull_ThrowsNPE() {
            thrown.expect(NullPointerException.class);

            ReaperModuleBuilder.create((Annotation) null);
        }

        @Test
        public void build_NoArguments_NotNull() {
            assertNotNull(builder.build());
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

        @Test
        public void createInjector_WithFrameworkAndExecutorProvidedAndNotBound_ThrowsCreationException() {
            thrown.expect(CreationException.class);

            Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                }
            }, ReaperModuleBuilder.create().exectuor(Key.get(ScheduledExecutorService.class)).build());
        }

        @Test
        public void createInjector_WithFrameworkAndExecutorProvidedAndBound_BindsReaper() {

            Injector inj = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(ScheduledExecutorService.class).toInstance(executorService);
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                }
            }, ReaperModuleBuilder.create().exectuor(Key.get(ScheduledExecutorService.class)).build());

            inj.getInstance(Key.get(Reaper.class, Curator.class));
        }

        @Test
        public void createInjector_WithFrameworkAndNoExecutorProvided_BindsReaper() {

            Injector inj = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                }
            }, ReaperModuleBuilder.create().leaderPath("/reaperleader").build());

            inj.getInstance(Key.get(Reaper.class, Curator.class));
        }

        @Test
        public void createInjector_WithoutFramework_ThrowsCreationException() {
            thrown.expect(CreationException.class);

            Guice.createInjector(ReaperModuleBuilder.create().build());
        }

        @Test
        public void createInjector_WithFramework_BindsReaperManager() {

            Injector inj = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                }
            }, ReaperModuleBuilder.create(Cultivar.class).build());

            inj.getInstance(Key.get(ReaperManager.class, Cultivar.class));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void createInjector_WithFramework_BindsReaperManagerToCuratorServices() {

            Injector inj = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                }
            }, ReaperModuleBuilder.create(Cultivar.class).build());

            assertTrue(((Set<CuratorService>) inj.getInstance(Key.get(Types.setOf(CuratorService.class)))).contains(inj
                    .getInstance(Key.get(ReaperManager.class, Cultivar.class))));
        }
    }

}
