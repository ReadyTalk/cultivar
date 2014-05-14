package org.waterprinciple.cultivar.locks;

import com.google.common.annotations.Beta;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

@Beta
public class LockModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Revoker.class).in(Singleton.class);
    }
}
