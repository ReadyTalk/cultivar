package com.readytalk.cultivar.namespace;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import com.google.common.annotations.Beta;
import com.google.common.base.Equivalence;

/**
 * Represents a Namespace object that can be instantiated.
 */
@Beta
class NamespaceImpl implements Namespace, Serializable {
    private static final long serialVersionUID = 1L;

    private static final int ANNOTATION_HASH_CONSTANT = 127;

    private final String value;

    public NamespaceImpl(final String value) {
        this.value = checkNotNull(value, "namespace");
    }

    public String value() {
        return this.value;
    }

    public int hashCode() {
        // See http://docs.oracle.com/javase/7/docs/api/java/lang/annotation/Annotation.html
        return (ANNOTATION_HASH_CONSTANT * "value".hashCode()) ^ value.hashCode();
    }

    public boolean equals(final Object o) {
        if (!(o instanceof Namespace)) {
            return false;
        }

        Namespace other = (Namespace) o;

        return Equivalence.equals().equivalent(value, other.value());
    }

    public String toString() {
        return "@" + Namespace.class.getName() + "(value=" + value + ")";
    }

    public Class<? extends Annotation> annotationType() {
        return Namespace.class;
    }
}
