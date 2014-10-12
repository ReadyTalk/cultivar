package com.readytalk.cultivar.namespace;

import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.Annotation;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.curator.framework.CuratorFramework;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Scopes;
import com.google.inject.util.Providers;
import com.readytalk.cultivar.Curator;
import com.readytalk.cultivar.internal.AnnotationHolder;
import com.readytalk.cultivar.internal.Private;

/**
 * Constructs a namespaced CuratorFramework with a given namespace and binds it to a target annotation.
 */
@NotThreadSafe
@Beta
public class NamespaceModuleBuilder {

    private Optional<String> namespaceValue = null;
    private AnnotationHolder annotationHolder = null;

    NamespaceModuleBuilder() {

    }

    public static NamespaceModuleBuilder create() {
        return new NamespaceModuleBuilder();
    }

    public NamespaceModuleBuilder newNamespace(@Nullable final String namespace) {
        this.namespaceValue = Optional.fromNullable(namespace);

        return this;
    }

    public NamespaceModuleBuilder targetAnnotation(final Annotation annotation) {
        this.annotationHolder = AnnotationHolder.create(annotation);

        return this;
    }

    public NamespaceModuleBuilder targetAnnotation(final Class<? extends Annotation> annotation) {
        this.annotationHolder = AnnotationHolder.create(annotation);

        return this;
    }

    public Module build() {
        checkState(namespaceValue != null, "Namespace not provided.");
        checkState(annotationHolder != null, "Target annotation not provided.");

        return new PrivateModule() {
            @Override
            protected void configure() {
                bind(CuratorFramework.class).annotatedWith(Private.class).to(
                        Key.get(CuratorFramework.class, Curator.class));

                bind(String.class).annotatedWith(Private.class).toProvider(Providers.of(namespaceValue.orNull()));

                Key<CuratorFramework> frameworkKey = annotationHolder.generateKey(CuratorFramework.class);

                bind(frameworkKey).to(NamespacedCuratorFramework.class).in(Scopes.SINGLETON);

                expose(frameworkKey);
            }
        };
    }
}
