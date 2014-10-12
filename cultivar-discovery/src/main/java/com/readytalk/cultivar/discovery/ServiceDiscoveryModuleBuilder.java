package com.readytalk.cultivar.discovery;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.Annotation;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceProviderBuilder;
import org.apache.curator.x.discovery.details.InstanceSerializer;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import com.readytalk.cultivar.AbstractModuleBuilder;
import com.readytalk.cultivar.Curator;
import com.readytalk.cultivar.CuratorService;
import com.readytalk.cultivar.internal.AnnotationHolder;
import com.readytalk.cultivar.internal.Private;

/**
 * Constructs a module that binds a ServiceDiscovery instance to a particular annotation. This is done to allow for one
 * service discovery data type (e.g., Void) to be used at several different paths without conflict.
 * 
 */
@Beta
@NotThreadSafe
@SuppressWarnings("unchecked")
public class ServiceDiscoveryModuleBuilder<T> extends AbstractModuleBuilder<ServiceDiscoveryModuleBuilder<T>> {

    private final Class<T> payloadClass;

    private String basePathValue;

    private AnnotationHolder annotation = null;

    private InstanceSerializer<T> serializerValue;

    @VisibleForTesting
    ServiceDiscoveryModuleBuilder(final Class<T> payloadClass) {
        this.payloadClass = payloadClass;
    }

    /**
     * Create a ServiceDiscoveryModuleBuilder instance off of a given payloadClass.
     */
    public static <T> ServiceDiscoveryModuleBuilder<T> create(final Class<T> payloadClass) {
        return new ServiceDiscoveryModuleBuilder<T>(checkNotNull(payloadClass));
    }

    /**
     * Create a ServiceDiscoveryModuleBuilder with a Void payload class.
     */
    public static ServiceDiscoveryModuleBuilder<Void> create() {
        return create(Void.class);
    }

    /**
     * The path in ZK to use with the module. Required.
     */
    public ServiceDiscoveryModuleBuilder<T> basePath(final String basePath) {
        this.basePathValue = checkNotNull(basePath);

        return this;
    }

    /**
     * The annotation to use with the binding. Required.
     */
    public ServiceDiscoveryModuleBuilder<T> annotation(final Annotation ann) {

        this.annotation = AnnotationHolder.create(ann);

        return this;

    }

    /**
     * The annotation to use with the binding. Required.
     */
    public ServiceDiscoveryModuleBuilder<T> annotation(final Class<? extends Annotation> ann) {

        this.annotation = AnnotationHolder.create(ann);

        return this;

    }

    /**
     * The serializer to use. Optional.
     */
    public ServiceDiscoveryModuleBuilder<T> serializer(final InstanceSerializer<T> serializer) {

        this.serializerValue = checkNotNull(serializer);

        return this;
    }

    /**
     * Build the module for Guice.
     */
    public Module build() {

        checkState(basePathValue != null, "basePath not set.");
        checkState(annotation != null, "annotation not set.");

        return new AbstractModule() {
            @Override
            protected void configure() {

                requireBinding(Key.get(CuratorFramework.class, Curator.class));

                final Key<ServiceDiscoveryManager<T>> manager = managerKey();

                install(new PrivateModule() {
                    @Override
                    protected void configure() {
                        ServiceDiscoveryModuleBuilder.this.bindFramework(binder());

                        bind(builderKey()).toInstance(ServiceDiscoveryBuilder.builder(payloadClass));

                        bind(privateDiscoveryKey()).toProvider(providerKey()).in(Singleton.class);

                        bindConstant().annotatedWith(Names.named("Cultivar.private.basePath")).to(basePathValue);

                        if (serializerValue != null) {
                            bind(serializerKey()).toInstance(serializerValue);
                        }

                        bind(manager).to(blankManagerKey()).in(Singleton.class);

                        expose(manager);

                        Key<ServiceDiscovery<T>> discovery = discoveryKey();

                        bind(discovery).to(privateDiscoveryKey()).in(Singleton.class);

                        expose(discovery);

                        Key<ServiceProviderBuilder<T>> serviceProviderBuilder = serviceProviderBuilderKey();

                        bind(serviceProviderBuilder)
                                .toProvider(
                                        (Key<ServiceProviderBuilderProvider<T>>) parameterizedKey(ServiceProviderBuilderProvider.class));

                        expose(serviceProviderBuilder);

                    }
                });

                Multibinder<CuratorService> services = Multibinder.newSetBinder(binder(), CuratorService.class);

                services.addBinding().to(manager);

            }

        };
    }

    private Key<?> parameterizedKey(final Class<?> clazz) {
        return Key.get(Types.newParameterizedType(clazz, payloadClass));
    }

    private Key<?> parameterizedAnnotatedKey(final Class<?> clazz) {
        return annotation.generateKey(Types.newParameterizedType(clazz, payloadClass));
    }

    private Key<ServiceDiscoveryManager<T>> blankManagerKey() {
        return (Key<ServiceDiscoveryManager<T>>) parameterizedKey(ServiceDiscoveryManager.class);
    }

    private Key<ServiceDiscoveryManager<T>> managerKey() {
        return (Key<ServiceDiscoveryManager<T>>) parameterizedAnnotatedKey(ServiceDiscoveryManager.class);
    }

    private Key<ServiceDiscovery<T>> privateDiscoveryKey() {
        return discoveryKey(Private.class);
    }

    private Key<ServiceDiscovery<T>> discoveryKey() {
        return (Key<ServiceDiscovery<T>>) parameterizedAnnotatedKey(ServiceDiscovery.class);
    }

    private Key<ServiceDiscovery<T>> discoveryKey(final Class<? extends Annotation> ann) {
        return (Key<ServiceDiscovery<T>>) Key
                .get(Types.newParameterizedType(ServiceDiscovery.class, payloadClass), ann);
    }

    private Key<ServiceDiscoveryProvider<T>> providerKey() {
        return (Key<ServiceDiscoveryProvider<T>>) parameterizedKey(ServiceDiscoveryProvider.class);
    }

    private Key<ServiceDiscoveryBuilder<T>> builderKey() {
        return (Key<ServiceDiscoveryBuilder<T>>) parameterizedKey(ServiceDiscoveryBuilder.class);
    }

    private Key<InstanceSerializer<T>> serializerKey() {
        return (Key<InstanceSerializer<T>>) parameterizedKey(InstanceSerializer.class);
    }

    private Key<ServiceProviderBuilder<T>> serviceProviderBuilderKey() {

        return (Key<ServiceProviderBuilder<T>>) parameterizedAnnotatedKey(ServiceProviderBuilder.class);
    }
}
