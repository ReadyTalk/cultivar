package com.readytalk.cultivar.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Key;

@Immutable
public class AnnotationHolder {

    private final Annotation annotation;

    private final Class<? extends Annotation> annotationClass;

    @VisibleForTesting
    AnnotationHolder(@Nullable final Annotation annotation, @Nullable final Class<? extends Annotation> annotationClass) {
        checkArgument(annotation != null || annotationClass != null);
        checkArgument(annotation == null || annotationClass == null);

        this.annotation = annotation;
        this.annotationClass = annotationClass;
    }

    public static AnnotationHolder create(final Annotation annotation) {
        return new AnnotationHolder(checkNotNull(annotation), null);
    }

    public static AnnotationHolder create(final Class<? extends Annotation> annotationClass) {
        return new AnnotationHolder(null, checkNotNull(annotationClass));
    }

    @SuppressWarnings("unchecked")
    public <T> Key<T> generateKey(final Class<T> clazz) {
        return (Key<T>) this.generateKey((Type) clazz);
    }

    public Key<?> generateKey(final Type type) {
        if (annotation != null) {
            return Key.get(type, annotation);
        } else {
            return Key.get(type, annotationClass);
        }
    }
}
