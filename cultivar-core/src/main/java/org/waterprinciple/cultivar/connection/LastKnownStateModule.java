package org.waterprinciple.cultivar.connection;

import org.apache.curator.framework.state.ConnectionStateListener;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

public class LastKnownStateModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LastKnownState.class).in(Singleton.class);

        bind(LastKnownStateListener.class).in(Singleton.class);

        Multibinder.newSetBinder(binder(), ConnectionStateListener.class).addBinding().to(LastKnownStateListener.class);

    }
}
