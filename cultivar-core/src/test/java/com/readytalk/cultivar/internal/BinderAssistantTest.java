package com.readytalk.cultivar.internal;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.readytalk.cultivar.internal.BinderAssistant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.util.Providers;

@RunWith(MockitoJUnitRunner.class)
public class BinderAssistantTest {

    public static class StringProvider implements Provider<Provider<String>> {
        @Override
        public Provider<String> get() {
            return Providers.of("test");
        }
    }

    private final Key<Provider<String>> key = Key.get(new TypeLiteral<Provider<String>>() {
    });

    @Mock
    private Binder binder;

    @Mock
    private LinkedBindingBuilder<Provider<String>> bindingBuilder;

    @Before
    public void setUp() {
        when(binder.bind(eq(key))).thenReturn(bindingBuilder);
    }

    @Test
    public void bindToProvider_Class_Binds() {
        new BinderAssistant<Provider<String>>(StringProvider.class).bindToProvider(binder, key);

        verify(bindingBuilder).toProvider(StringProvider.class);
    }

    @Test
    public void bindToProvider_Instance_Binds() {
        Provider<Provider<String>> instance = new StringProvider();

        new BinderAssistant<Provider<String>>(instance).bindToProvider(binder, key);

        verify(bindingBuilder).toProvider(instance);
    }

    @Test
    public void bindToProvider_TypeLiteral_Binds() {
        TypeLiteral<Provider<Provider<String>>> typeLiteral = new TypeLiteral<Provider<Provider<String>>>() {
        };

        new BinderAssistant<Provider<String>>(typeLiteral).bindToProvider(binder, key);

        verify(bindingBuilder).toProvider(typeLiteral);
    }

    @Test
    public void bindToProvider_Key_Binds() {
        Key<StringProvider> typeKey = Key.get(StringProvider.class);

        new BinderAssistant<Provider<String>>(typeKey).bindToProvider(binder, key);

        verify(bindingBuilder).toProvider(typeKey);
    }
}
