package com.readytalk.cultivar;

import java.lang.annotation.Annotation;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.curator.framework.CuratorFramework;

import com.google.common.annotations.Beta;
import com.google.inject.Binder;
import com.readytalk.cultivar.internal.AnnotationHolder;
import com.readytalk.cultivar.internal.Private;

@NotThreadSafe
@Beta
public abstract class AbstractModuleBuilder<T extends AbstractModuleBuilder<T>> implements ModuleBuilder<T> {

    private AnnotationHolder annotationHolder = AnnotationHolder.create(Curator.class);

    @Override
    @SuppressWarnings("unchecked")
    public T framework(final Annotation annotation) {

        annotationHolder = AnnotationHolder.create(annotation);

        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T framework(final Class<? extends Annotation> annotation) {
        annotationHolder = AnnotationHolder.create(annotation);

        return (T) this;
    }

    protected final void bindFramework(final Binder binder) {
        binder.bind(CuratorFramework.class).annotatedWith(Private.class)
                .to(annotationHolder.generateKey(CuratorFramework.class));
    }
}
