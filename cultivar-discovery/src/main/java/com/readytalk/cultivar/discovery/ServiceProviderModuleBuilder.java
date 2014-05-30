package com.readytalk.cultivar.discovery;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.annotations.Beta;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Types;
import com.readytalk.cultivar.CuratorService;
import com.readytalk.cultivar.internal.AnnotationHolder;
import com.readytalk.cultivar.internal.Private;

import org.apache.curator.x.discovery.DownInstancePolicy;
import org.apache.curator.x.discovery.InstanceFilter;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.ServiceProviderBuilder;

@Beta
@NotThreadSafe
public class ServiceProviderModuleBuilder<T> {

    private final Class<T> payloadClass;

    private String serviceName = null;

    private AnnotationHolder discoveryAnnotation = null;

    private AnnotationHolder targetAnnotation = null;

    private final Set<InstanceFilter<T>> instanceFilters = Sets.newLinkedHashSet();

    private ProviderStrategy<T> strategy = null;

    private ThreadFactory threads = null;

    private DownInstancePolicy downInstancePolicy = null;

    ServiceProviderModuleBuilder(final Class<T> payloadClass) {
        this.payloadClass = payloadClass;
    }

    public static ServiceProviderModuleBuilder<Void> create() {
        return create(Void.class);
    }

    public static <T> ServiceProviderModuleBuilder<T> create(final Class<T> payloadClass) {
        return new ServiceProviderModuleBuilder<T>(checkNotNull(payloadClass));
    }

    public ServiceProviderModuleBuilder<T> name(final String name) {

        this.serviceName = checkNotNull(name);

        return this;
    }

    public ServiceProviderModuleBuilder<T> discovery(final Annotation ann) {

        discoveryAnnotation = AnnotationHolder.create(ann);

        return this;
    }

    public ServiceProviderModuleBuilder<T> discovery(final Class<? extends Annotation> ann) {

        discoveryAnnotation = AnnotationHolder.create(ann);

        return this;
    }

    public ServiceProviderModuleBuilder<T> annotation(final Annotation ann) {

        targetAnnotation = AnnotationHolder.create(ann);

        return this;
    }

    public ServiceProviderModuleBuilder<T> annotation(final Class<? extends Annotation> ann) {

        targetAnnotation = AnnotationHolder.create(ann);

        return this;
    }

    public ServiceProviderModuleBuilder<T> additionalFilter(final InstanceFilter<T> filter) {

        instanceFilters.add(checkNotNull(filter));

        return this;
    }

    public ServiceProviderModuleBuilder<T> providerStrategy(final ProviderStrategy<T> providerStrategy) {
        this.strategy = checkNotNull(providerStrategy);

        return this;
    }

    public ServiceProviderModuleBuilder<T> threadFactory(final ThreadFactory threadFactory) {
        this.threads = checkNotNull(threadFactory);

        return this;
    }

    public ServiceProviderModuleBuilder<T> downInstancePolicy(final DownInstancePolicy policy) {

        this.downInstancePolicy = checkNotNull(policy);

        return this;
    }

    @SuppressWarnings("unchecked")
    public Module build() {
        checkState(serviceName != null, "name not set.");
        checkState(discoveryAnnotation != null, "Annotation for discovery not set.");
        checkState(targetAnnotation != null, "Target annotation not set.");

        return new AbstractModule() {
            @Override
            protected void configure() {
                requireBinding(discoveryAnnotation.generateKey(Types.newParameterizedType(ServiceProviderBuilder.class,
                        payloadClass)));

                final Key<ServiceProviderManager<T>> manager = (Key<ServiceProviderManager<T>>) targetAnnotation
                        .generateKey(Types.newParameterizedType(ServiceProviderManager.class, payloadClass));

                install(new PrivateModule() {
                    @Override
                    protected void configure() {
                        AnnotationHolder privateAnnotation = AnnotationHolder.create(Private.class);

                        Multibinder<InstanceFilter<T>> filters = Multibinder.newSetBinder(
                                binder(),
                                (TypeLiteral<InstanceFilter<T>>) Key.get(
                                        Types.newParameterizedType(InstanceFilter.class, payloadClass))
                                        .getTypeLiteral(), Private.class);

                        for (InstanceFilter<T> o : instanceFilters) {
                            filters.addBinding().toInstance(o);
                        }

                        if (strategy != null) {
                            bind(
                                    (Key<ProviderStrategy<T>>) Key.get(
                                            Types.newParameterizedType(ProviderStrategy.class, payloadClass),
                                            Private.class)).toInstance(strategy);
                        }

                        if (threads != null) {
                            bind(ThreadFactory.class).annotatedWith(Private.class).toInstance(threads);
                        }

                        if (downInstancePolicy != null) {
                            bind(DownInstancePolicy.class).annotatedWith(Private.class).toInstance(downInstancePolicy);
                        }

                        bind(
                                (Key<ServiceProviderBuilder<T>>) privateAnnotation.generateKey(Types
                                        .newParameterizedType(ServiceProviderBuilder.class, payloadClass))).to(
                                (Key<ServiceProviderBuilder<T>>) discoveryAnnotation.generateKey(Types
                                        .newParameterizedType(ServiceProviderBuilder.class, payloadClass)));

                        bindConstant().annotatedWith(Private.class).to(serviceName);

                        Key<ServiceProvider<T>> target = (Key<ServiceProvider<T>>) targetAnnotation.generateKey(Types
                                .newParameterizedType(ServiceProvider.class, payloadClass));

                        bind(target).toProvider(
                                (Key<ServiceProviderProvider<T>>) Key.get(Types.newParameterizedType(
                                        ServiceProviderProvider.class, payloadClass))).in(Singleton.class);

                        expose(target);

                        bind(
                                (Key<ServiceProvider<T>>) privateAnnotation.generateKey(Types.newParameterizedType(
                                        ServiceProvider.class, payloadClass))).to(target).in(Singleton.class);

                        bind(manager).to(
                                (Key<ServiceProviderManager<T>>) Key.get(Types.newParameterizedType(
                                        ServiceProviderManager.class, payloadClass))).in(Singleton.class);

                        expose(manager);

                    }
                });

                Multibinder.newSetBinder(binder(), CuratorService.class).addBinding().to(manager);
            }
        };
    }
}
