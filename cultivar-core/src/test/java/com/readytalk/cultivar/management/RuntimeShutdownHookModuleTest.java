package com.readytalk.cultivar.management;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

@RunWith(MockitoJUnitRunner.class)
public class RuntimeShutdownHookModuleTest {

    @Mock
    private Runtime runtime;

    @Mock
    private Runnable runnable;

    @Test
    public void createInjector_NoParameters_Succeeds() {
        assertNotNull(Guice.createInjector(new RuntimeShutdownHookModule()));
    }

    @Test
    public void createInjector_BoundRuntime_BindsRuntime() {
        Guice.createInjector(new RuntimeShutdownHookModule(), new AbstractModule() {
            @Override
            protected void configure() {
                bind(Runtime.class).toInstance(runtime);
            }
        }).getInstance(ShutdownManager.class).registerHook(runnable);

        verify(runtime).addShutdownHook(any(Thread.class));
    }
}
