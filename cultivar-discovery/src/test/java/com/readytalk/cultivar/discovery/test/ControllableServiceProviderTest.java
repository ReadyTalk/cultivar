package com.readytalk.cultivar.discovery.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.curator.x.discovery.ServiceInstance;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

@SuppressWarnings("unchecked")
public class ControllableServiceProviderTest {

    private ServiceInstance<Void> instance;

    private ControllableServiceProvider<Void> provider;

    @Before
    public void setUp() throws Exception {
        instance = ServiceInstance.<Void> builder().name("name").id("id").build();
        provider = new ControllableServiceProvider<Void>();
    }

    @Test
    public void getInstance_NoInstances_ReturnsNull() {
        assertNull(provider.getInstance());
    }

    @Test
    public void getAllInstances_NoInstances_ReturnsEmptyCollection() {
        assertEquals(0, provider.getAllInstances().size());
    }

    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    @Test
    public void noteError_instance_ReturnsOnGetErrors() {
        provider.noteError(instance);

        assertEquals(Lists.newArrayList(instance), provider.getErrors());
    }

    @Test
    public void addInstance_instance_ReturnsOnGetInstance() {
        provider.addInstance(instance);

        assertEquals(instance, provider.getInstance());
    }

    @Test
    public void removeInstance_instance_DoesNotReturnOnGetInstance() {
        provider.addInstance(instance);
        provider.removeInstance(instance);

        assertNull(provider.getInstance());
    }
}
