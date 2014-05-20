package com.readytalk.cultivar.ensemble;

import java.util.Map;
import java.util.Properties;

import org.apache.curator.ensemble.EnsembleProvider;
import com.readytalk.cultivar.internal.Private;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.inject.util.Types;

/**
 * Binds an EnsembleProvider.
 * <ul>
 * <li>If no exhibitor instances are provider (server property cultivar.zookeeper.exhibitor) then it uses a fixed
 * provider.</li>
 * <li>If no connection string is provided it crashes.</li>
 * </ul>
 */
@Beta
public class EnsembleProviderModule extends PrivateModule {

    @Override
    @SuppressWarnings("unchecked")
    protected void configure() {

        bind(
                (Key<Optional<String>>) Key.get(Types.newParameterizedType(Optional.class, String.class),
                        Names.named("Cultivar.private.properties.backupConnections"))).toProvider(
                ConnectionProvider.class);

        bind(
                (Key<Optional<String>>) Key.get(Types.newParameterizedType(Optional.class, String.class),
                        Names.named("Cultivar.private.properties.exhibitorInstances"))).toProvider(
                ExhibitorProvider.class);

        bind(EnsembleProvider.class).toProvider(EnsembleProviderProvider.class).in(Singleton.class);

        expose(EnsembleProvider.class);
    }

    @Provides
    @Private
    public Map<String, String> environment() {
        return System.getenv();
    }

    @Provides
    @Private
    public Properties properties() {
        return System.getProperties();
    }

}
