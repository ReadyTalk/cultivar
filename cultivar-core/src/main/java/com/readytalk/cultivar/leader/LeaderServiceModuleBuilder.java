package com.readytalk.cultivar.leader;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.concurrent.NotThreadSafe;

import com.readytalk.cultivar.CuratorService;

import com.google.common.annotations.Beta;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;

@NotThreadSafe
@Beta
public final class LeaderServiceModuleBuilder<T extends LeaderService> {

    private final Key<T> key;

    private TypeLiteral<? extends T> implementation = null;

    private Module dependencies = null;

    private LeaderServiceModuleBuilder(final Key<T> key) {
        this.key = checkNotNull(key);
    }

    public LeaderServiceModuleBuilder<T> implementation(final TypeLiteral<? extends T> clazz) {
        this.implementation = checkNotNull(clazz);

        return this;
    }

    public LeaderServiceModuleBuilder<T> implementation(final Class<? extends T> clazz) {
        return implementation(TypeLiteral.get(clazz));
    }

    public static <T extends LeaderService> LeaderServiceModuleBuilder<T> create(final Key<T> key) {
        return new LeaderServiceModuleBuilder<T>(checkNotNull(key));
    }

    public LeaderServiceModuleBuilder<T> dependencies(final Module... modules) {
        checkState(dependencies == null, "Dependencies were already set.");
        checkArgument(modules.length > 0);

        this.dependencies = Modules.combine(checkNotNull(modules));

        return this;
    }

    public Module build() {

        checkState(implementation != null, "No implementation set for %s", key);

        return new AbstractModule() {
            @Override
            protected void configure() {
                install(new PrivateModule() {
                    @Override
                    protected void configure() {
                        install(dependencies);

                        bind(key).to(implementation).in(Singleton.class);
                        expose(key);
                    }
                });

                Multibinder<CuratorService> curatorServices = Multibinder.newSetBinder(binder(), CuratorService.class);

                curatorServices.addBinding().to(key);
            }
        };
    }
}
