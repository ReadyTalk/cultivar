package com.readytalk.cultivar.ensemble;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.inject.util.Types;

import org.apache.curator.ensemble.EnsembleProvider;

/**
 * Binds an EnsembleProvider.
 * <ul>
 * <li>If no exhibitor instances are provider (server property cultivar.zookeeper.exhibitor) then it uses a fixed
 * provider.</li>
 * <li>If no connection string is provided it crashes.</li>
 * </ul>
 * 
 * Mandatory Bindings:
 * 
 * Optional Bindings:
 * 
 * <ul>
 * <li>Cultivar.properties.exhibitor.restPort : int (8080)</li>
 * <li>Cultivar.properties.exhibitor.restPath : String (/exhibitor/v1/cluster/list)</li>
 * <li>Cultivar.properties.exhibitor.pollingTimeMillis : int (5 minutes)</li>
 * <li>Cultivar.properties.exhibitor.retryPolicy : RetryPolicy</li>
 * <li>: ExhibitorRestClient (DefaultExhibitorRestClient(false))</li>
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

}
