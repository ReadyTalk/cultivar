package com.readytalk.cultivar.locks;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.readytalk.cultivar.Curator;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scopes;

@RunWith(Enclosed.class)
public class LockModuleTest {

    public static class ModuleTest {
        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Before
        public void setUp() throws Exception {

        }

        @Test
        public void createInjector_NoCuratorFramework_ThrowsCreationException() {
            thrown.expect(CreationException.class);

            Guice.createInjector(new LockModule());
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class InjectorTest {
        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Mock
        private CuratorFramework framework;

        private Injector inj;

        @Before
        public void setUp() throws Exception {
            inj = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CuratorFramework.class).annotatedWith(Curator.class).toInstance(framework);

                }
            }, new LockModule());
        }

        @Test
        public void getBinding_Revoker_IsSingleton() {
            Binding<Revoker> binding = inj.getExistingBinding(Key.get(Revoker.class));

            assertNotNull("Not bound.", binding);

            assertTrue("Not a singleton.", Scopes.isSingleton(binding));
        }
    }

}
