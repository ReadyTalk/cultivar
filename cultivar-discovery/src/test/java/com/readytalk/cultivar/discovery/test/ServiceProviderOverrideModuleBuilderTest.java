package com.readytalk.cultivar.discovery.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.readytalk.cultivar.internal.Private;

public class ServiceProviderOverrideModuleBuilderTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void create_GeneratesBuilder() {
        assertNotNull(ServiceProviderOverrideModuleBuilder.create());
    }

    @Test
    public void build_NoAnnotationSet_ThrowsISE() {
        thrown.expect(IllegalStateException.class);
        ServiceProviderOverrideModuleBuilder.create().build();
    }

    @Test
    public void build_NoProviderSet_ThrowsISE() {
        thrown.expect(IllegalStateException.class);
        ServiceProviderOverrideModuleBuilder.create().annotation(Private.class).build();
    }

    @Test
    public void build_AnnotationAndProviderSet_NoException() {
        ServiceProviderOverrideModuleBuilder.create().annotation(Private.class)
                .serviceProviderInstance(new ControllableServiceProvider<Void>()).build();
    }
}
