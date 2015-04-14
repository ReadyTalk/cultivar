package com.readytalk.cultivar.discovery.test;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

@ThreadSafe
public class ControllableServiceProvider<T> implements ServiceProvider<T> {

    private final Set<ServiceInstance<T>> instances = new CopyOnWriteArraySet<ServiceInstance<T>>();

    private final List<ServiceInstance<T>> errors = new CopyOnWriteArrayList<ServiceInstance<T>>();

    private final Random random = new Random();

    @Inject
    public ControllableServiceProvider() {

    }

    @Override
    public void start() throws Exception {

    }

    @SuppressWarnings("unchecked")
    @Override
    public ServiceInstance<T> getInstance() {

        Object[] retArray = instances.toArray();

        if (retArray.length == 0) {
            return null;
        }

        return (ServiceInstance<T>) retArray[random.nextInt(retArray.length)];
    }

    @Override
    public ImmutableCollection<ServiceInstance<T>> getAllInstances() {
        return ImmutableSet.copyOf(instances);
    }

    @Override
    public void noteError(final ServiceInstance<T> instance) {
        errors.add(instance);
    }

    @Override
    public void close() throws IOException {

    }

    public ImmutableList<ServiceInstance<T>> getErrors() {
        return ImmutableList.copyOf(errors);
    }

    public boolean addInstance(final ServiceInstance<T> instance) {
        return instances.add(instance);
    }

    public boolean removeInstance(final ServiceInstance<T> instance) {
        return instances.remove(instance);
    }
}
