package com.readytalk.cultivar.namespace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.readytalk.cultivar.Curator;

@RunWith(MockitoJUnitRunner.class)
public class NamespacesTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private CuratorFramework framework;

    @Test
    public void namespace_String_ReturnsNotNull() {
        assertNotNull(Namespaces.namespace("foo"));
    }

    @Test
    public void namespace_EqualStrings_ReturnsEqualValues() {
        assertEquals(Namespaces.namespace("foo"), Namespaces.namespace("foo"));
    }

    @Test
    public void bindNamespaces_ArrayOfNamespaces_BindsToCorrectAnnotations() {
        Injector inj = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                Namespaces.bindNamespaces(binder(), "foo", "bar", "baz");
            }
        });

        assertNotNull(inj.getInstance(Key.get(CuratorFramework.class, Namespaces.namespace("foo"))));
    }

    @Test
    public void bindNamespaces_SetOfNamespaces_BindsToCorrectAnnotations() {
        Injector inj = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                Namespaces.bindNamespaces(binder(), ImmutableSet.of("foo", "bar"));
            }
        });

        assertNotNull(inj.getInstance(Key.get(CuratorFramework.class, Namespaces.namespace("foo"))));
    }

    @Test
    public void bindNamespaces_Repeated_ThrowsCreationException() {
        thrown.expect(CreationException.class);

        Injector inj = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                Namespaces.bindNamespaces(binder(), "foo");

                Namespaces.bindNamespaces(binder(), "foo");
            }
        });

        inj.getInstance(Key.get(CuratorFramework.class, Namespaces.namespace("foo")));
    }

}
