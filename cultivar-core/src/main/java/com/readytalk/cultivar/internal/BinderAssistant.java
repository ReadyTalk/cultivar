package com.readytalk.cultivar.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Provider;

import com.google.common.annotations.Beta;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;

@Beta
public class BinderAssistant<T> {
    private final Class<? extends Provider<? extends T>> providerClass;
    private final Key<? extends Provider<? extends T>> providerKey;
    private final Provider<? extends T> providerInstance;
    private final TypeLiteral<? extends Provider<? extends T>> providerLiteral;

    public BinderAssistant(final Class<? extends Provider<? extends T>> provider) {
        this.providerClass = checkNotNull(provider);
        this.providerKey = null;
        this.providerInstance = null;
        this.providerLiteral = null;
    }

    public BinderAssistant(final Key<? extends Provider<? extends T>> provider) {
        this.providerClass = null;
        this.providerKey = checkNotNull(provider);
        this.providerInstance = null;
        this.providerLiteral = null;
    }

    public BinderAssistant(final Provider<? extends T> provider) {
        this.providerClass = null;
        this.providerKey = null;
        this.providerInstance = checkNotNull(provider);
        this.providerLiteral = null;
    }

    public BinderAssistant(final TypeLiteral<? extends Provider<? extends T>> provider) {
        this.providerClass = null;
        this.providerKey = null;
        this.providerInstance = null;
        this.providerLiteral = checkNotNull(provider);
    }

    public void bindToProvider(final Binder binder, final Key<T> key) {

        if (providerClass != null) {
            binder.bind(key).toProvider(providerClass);
        } else if (providerKey != null) {
            binder.bind(key).toProvider(providerKey);
        } else if (providerInstance != null) {
            binder.bind(key).toProvider(Providers.guicify(providerInstance));
        } else if (providerLiteral != null) {
            binder.bind(key).toProvider(providerLiteral);
        }
    }

}
