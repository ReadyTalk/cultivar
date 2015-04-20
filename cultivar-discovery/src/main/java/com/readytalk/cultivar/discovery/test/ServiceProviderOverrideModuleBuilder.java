package com.readytalk.cultivar.discovery.test;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.Annotation;

import org.apache.curator.x.discovery.ServiceProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.util.Types;
import com.readytalk.cultivar.discovery.ServiceProviderManager;
import com.readytalk.cultivar.internal.AnnotationHolder;

public class ServiceProviderOverrideModuleBuilder<T> {

    private final Class<T> payloadClass;

    private AnnotationHolder targetAnnotation = null;
    private ServiceProvider<T> provider = null;

    ServiceProviderOverrideModuleBuilder(final Class<T> payloadClass) {
        this.payloadClass = payloadClass;
    }

    public static ServiceProviderOverrideModuleBuilder<Void> create() {
        return create(Void.class);
    }

    public static <T> ServiceProviderOverrideModuleBuilder<T> create(final Class<T> payloadClass) {
        return new ServiceProviderOverrideModuleBuilder<T>(checkNotNull(payloadClass));
    }

    public ServiceProviderOverrideModuleBuilder<T> annotation(final Annotation ann) {

        targetAnnotation = AnnotationHolder.create(ann);

        return this;
    }

    public ServiceProviderOverrideModuleBuilder<T> annotation(final Class<? extends Annotation> ann) {

        targetAnnotation = AnnotationHolder.create(ann);

        return this;
    }

    public ServiceProviderOverrideModuleBuilder<T> serviceProviderInstance(final ServiceProvider<T> prov) {

        this.provider = prov;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Module build() {
        checkState(provider != null, "Provider must be provided.");
        checkState(targetAnnotation != null, "Target annotation must be set.");

        return new AbstractModule() {
            @Override
            protected void configure() {
                Key<ServiceProvider<T>> target = (Key<ServiceProvider<T>>) targetAnnotation.generateKey(Types
                        .newParameterizedType(ServiceProvider.class, payloadClass));

                Key<ServiceProviderManager<T>> manager = (Key<ServiceProviderManager<T>>) targetAnnotation
                        .generateKey(Types.newParameterizedType(ServiceProviderManager.class, payloadClass));

                bind(target).toInstance(provider);
                bind(manager).to(
                        (Key<ServiceProviderManager<T>>) Key.get(Types.newParameterizedType(
                                ControllableServiceProviderManager.class, payloadClass))).in(Singleton.class);
            }
        };
    }
}
