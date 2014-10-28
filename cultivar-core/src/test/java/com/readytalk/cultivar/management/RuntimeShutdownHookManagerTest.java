package com.readytalk.cultivar.management;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@SuppressWarnings("ConstantConditions")
@RunWith(MockitoJUnitRunner.class)
public class RuntimeShutdownHookManagerTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private Runtime runtime;

    @Mock
    private Runnable runnable;

    private RuntimeShutdownHookManager manager;

    @Before
    public void setUp() {
        this.manager = new RuntimeShutdownHookManager();

        manager.setRuntime(runtime);
    }

    @Test
    public void setRuntime_NullRuntime_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        manager.setRuntime(null);
    }

    @Test
    public void registerHook_NullHook_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        manager.registerHook(null);
    }

    @Test
    public void registerHook_WithHook_RegistersAgainstRuntime() {
        manager.registerHook(runnable);

        verify(runtime).addShutdownHook(any(Thread.class));
    }
}