package com.readytalk.cultivar.cache;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.Annotation;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.curator.framework.recipes.cache.NodeCache;

import com.google.common.base.Optional;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import com.readytalk.cultivar.AbstractModuleBuilder;
import com.readytalk.cultivar.CuratorService;
import com.readytalk.cultivar.internal.AnnotationHolder;
import com.readytalk.cultivar.internal.Private;
import com.readytalk.cultivar.util.mapping.ByteArrayMapper;
import com.readytalk.cultivar.util.mapping.NOPByteArrayMapper;

@NotThreadSafe
public class NodeContainerModuleBuilder<T> extends AbstractModuleBuilder<NodeContainerModuleBuilder<T>> {

    private final Class<T> objectType;
    private boolean compressed = false;

    private Class<? extends ByteArrayMapper<T>> mapper = null;
    private String path = null;
    private String override = null;
    private AnnotationHolder annotationHolder = null;

    NodeContainerModuleBuilder(final Class<T> objectType) {
        this.objectType = objectType;
    }

    public static <T> NodeContainerModuleBuilder<T> create(final Class<T> objectType) {
        return new NodeContainerModuleBuilder<T>(checkNotNull(objectType));
    }

    /**
     * Creates a NodeContainerModuleBuilder with the default (byte[]) type and with a NOP mapper.
     */
    public static NodeContainerModuleBuilder<byte[]> create() {
        return create(byte[].class).mapper(NOPByteArrayMapper.class);
    }

    public NodeContainerModuleBuilder<T> annotation(final Class<? extends Annotation> ann) {
        annotationHolder = AnnotationHolder.create(ann);

        return this;
    }

    public NodeContainerModuleBuilder<T> annotation(final Annotation ann) {
        annotationHolder = AnnotationHolder.create(ann);

        return this;
    }

    public NodeContainerModuleBuilder<T> path(final String nodePath) {
        this.path = checkNotNull(nodePath);

        return this;
    }

    public NodeContainerModuleBuilder<T> overrideProperty(final String nodeOverride) {
        this.override = checkNotNull(nodeOverride);

        return this;
    }

    public NodeContainerModuleBuilder<T> compressed() {
        this.compressed = true;

        return this;
    }

    public NodeContainerModuleBuilder<T> mapper(final Class<? extends ByteArrayMapper<T>> mapperClass) {
        this.mapper = checkNotNull(mapperClass);

        return this;
    }

    public Module build() {
        checkState(mapper != null, "Mapper has not been set.");
        checkState(path != null, "Path has not been set.");
        checkState(annotationHolder != null, "Annotation has not been set.");

        return new AbstractModule() {
            @Override
            @SuppressWarnings("unchecked")
            protected void configure() {
                final Key<NodeContainer<T>> nodeContainerKey = (Key<NodeContainer<T>>) annotationHolder
                        .generateKey(Types.newParameterizedType(NodeContainer.class, objectType));

                install(new PrivateModule() {
                    @Override
                    protected void configure() {
                        NodeContainerModuleBuilder.this.bindFramework(binder());

                        bind(NodeCache.class).annotatedWith(Private.class).toProvider(NodeCacheProvider.class)
                                .in(Singleton.class);
                        bindConstant().annotatedWith(Names.named("Cultivar.cache.path")).to(path);
                        bindConstant().annotatedWith(Names.named("Cultivar.cache.compressed")).to(compressed);

                        bind(
                                (Key<ByteArrayMapper<T>>) Key.get(
                                        Types.newParameterizedType(ByteArrayMapper.class, objectType), Private.class))
                                .to(mapper);

                        bind(nodeContainerKey).to(
                                (Key<DefaultNodeContainer<T>>) Key.get(Types.newParameterizedType(
                                        DefaultNodeContainer.class, objectType))).in(Singleton.class);
                        expose(nodeContainerKey);
                    }

                    @Private
                    @Provides
                    @Singleton
                    public Optional<String> overrideProperty() {
                        return Optional.fromNullable(override);
                    }
                });

                Multibinder.newSetBinder(binder(), CuratorService.class).addBinding().to(nodeContainerKey);
            }
        };
    }
}
