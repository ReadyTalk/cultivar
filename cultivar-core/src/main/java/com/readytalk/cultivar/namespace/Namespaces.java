package com.readytalk.cultivar.namespace;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;

/**
 * Provides easy support for using the Namespace annotation on classes, allowing for easy definition and use of
 * namespaced CuratorFramework instances.
 */
@Beta
public final class Namespaces {

    private Namespaces() {

    }

    /**
     * Creates a Namespace annotation object with a given name.
     */
    public static Namespace namespace(final String name) {
        return new NamespaceImpl(name);
    }

    /**
     * Binds an array of namespaced CuratorFramework instances within the binder.
     */
    public static void bindNamespaces(final Binder binder, final String... namespaces) {
        bindNamespaces(binder, ImmutableSet.copyOf(namespaces));
    }

    /**
     * Binds a set of namespaced CuratorFramework instances within the binder.
     */
    public static void bindNamespaces(final Binder binder, final Set<String> namespaces) {

        for (String o : namespaces) {
            String op = o;
            String value = o;

            if (Strings.isNullOrEmpty(value)) {
                value = null;
                op = "";
            }

            binder.install(NamespaceModuleBuilder.create().newNamespace(value)
                    .targetAnnotation(Namespaces.namespace(op)).build());
        }
    }
}
