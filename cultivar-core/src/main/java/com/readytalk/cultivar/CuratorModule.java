package com.readytalk.cultivar;

import java.util.Set;

import javax.annotation.Nullable;

import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.readytalk.cultivar.barriers.BarrierModule;
import com.readytalk.cultivar.connection.LastKnownStateModule;
import com.readytalk.cultivar.internal.Private;

/**
 * The method for creating the Curator instance.
 * 
 * Mandatory Bindings:
 * <ul>
 * <li>@Curator : EnsembleProvider</li>
 * <li>@Curator : RetryPolicy</li>
 * </ul>
 * 
 * Optional Bindings:
 * 
 * <ul>
 * <li>Cultivar.Curator.connectionTimeoutMs : int</li>
 * <li>Cultivar.Curator.auth.scheme : String</li>
 * <li>Cultivar.Curator.auth.auth : byte []</li>
 * <li>Cultivar.Curator.defaultData : byte []</li>
 * <li>Cultivar.Curator.baseNamespace : String</li>
 * <li>Cultivar.Curator.sessionTimeoutMs : int</li>
 * <li>Cultivar.Curator.canBeReadOnly : boolean</li>
 * <li>@Curator : ThreadFactory</li>
 * <li>@Curator : ZookeeperFactory</li>
 * <li>@Curator : TracerDriver</li>
 * </ul>
 * 
 */
@Beta
public class CuratorModule extends AbstractModule {
    private final Module dependencies;

    public CuratorModule() {
        this(null);
    }

    public CuratorModule(@Nullable final Module dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    protected void configure() {
        install(new CuratorInnerModule(dependencies));
        install(new BarrierModule());
        install(new LastKnownStateModule());

        bind(BlankCuratorService.class).annotatedWith(Private.class).to(BlankCuratorService.class).in(Scopes.SINGLETON);

        Multibinder.newSetBinder(binder(), CuratorService.class).addBinding()
                .to(Key.get(BlankCuratorService.class, Private.class));

        Multibinder.newSetBinder(binder(), UnhandledErrorListener.class);

        Multibinder.newSetBinder(binder(), CuratorListener.class);

        Multibinder.newSetBinder(binder(), ServiceManager.class, Cultivar.class);

        Multibinder<ConnectionStateListener> connectionStateListeners = Multibinder.newSetBinder(binder(),
                ConnectionStateListener.class);

        connectionStateListeners.addBinding().to(ConnectionStateLogger.class).in(Singleton.class);

        bind(CultivarStartStopManager.class).to(DefaultCultivarStartStopManager.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    @Cultivar
    public ServiceManager manager(final Set<CuratorService> services) {
        return new ServiceManager(services);
    }
}
