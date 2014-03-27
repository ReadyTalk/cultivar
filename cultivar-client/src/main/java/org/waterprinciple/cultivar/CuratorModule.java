package org.waterprinciple.cultivar;

import java.util.Set;

import javax.annotation.Nullable;

import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

/**
 * The method for creating the Curator instance.
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

        Multibinder.newSetBinder(binder(), CuratorService.class);

        Multibinder.newSetBinder(binder(), UnhandledErrorListener.class);

        Multibinder.newSetBinder(binder(), CuratorListener.class);

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
