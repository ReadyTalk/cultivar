package com.readytalk.cultivar.discovery;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.readytalk.cultivar.Cultivar;

@Beta
public class RegistrationModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), new TypeLiteral<RegistrationService<?>>() {
        });

        Multibinder.newSetBinder(binder(), ServiceManager.class, Cultivar.class).addBinding()
                .toProvider(getProvider(Key.get(ServiceManager.class, Discovery.class)));
    }

    @Provides
    @Discovery
    @Singleton
    public ServiceManager manager(final Set<RegistrationService<?>> registrationServices) {
        return new ServiceManager(registrationServices);
    }
}
