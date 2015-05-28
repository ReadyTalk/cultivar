package com.readytalk.cultivar;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.readytalk.cultivar.internal.Private;

@RunWith(MockitoJUnitRunner.class)
public class AbstractModuleBuilderTest {

    public static class ExampleModuleBuilder extends AbstractModuleBuilder<ExampleModuleBuilder> {
        @Override
        public Module build() {
            return null;
        }
    }

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private CuratorFramework framework;

    @Mock
    private CuratorFramework alternateFramework;

    @Test
    public void bindFrameworkModule_NoFrameworkSet_BindsCuratorToPrivate() {
        Injector inj = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);
                new ExampleModuleBuilder().bindFramework(binder());
            }
        });

        assertEquals(framework, inj.getInstance(Key.get(CuratorFramework.class, Private.class)));
    }

    @Test
    public void bindFrameworkModule_NamedFramework_BindsNamedToPrivate() {
        Injector inj = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);
                bind(CuratorFramework.class).annotatedWith(Names.named("foo")).toInstance(alternateFramework);

                new ExampleModuleBuilder().framework(Names.named("foo")).bindFramework(binder());
            }
        });

        assertEquals(alternateFramework, inj.getInstance(Key.get(CuratorFramework.class, Private.class)));
    }

    @Test
    public void bindFrameworkModule_ClassAnnotatedFramework_BindsNamedToPrivate() {
        Injector inj = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);
                bind(CuratorFramework.class).annotatedWith(Cultivar.class).toInstance(alternateFramework);

                new ExampleModuleBuilder().framework(Cultivar.class).bindFramework(binder());
            }
        });

        assertEquals(alternateFramework, inj.getInstance(Key.get(CuratorFramework.class, Private.class)));
    }

    @Test
    public void framework_NullAnnotation_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        new ExampleModuleBuilder().framework((Annotation) null);
    }

    @Test
    public void framework_NullClass_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        new ExampleModuleBuilder().framework((Class<? extends Annotation>) null);
    }
}
