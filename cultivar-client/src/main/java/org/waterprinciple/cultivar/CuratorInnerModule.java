package org.waterprinciple.cultivar;

import java.lang.annotation.Annotation;

import javax.annotation.Nullable;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;

import com.google.common.annotations.Beta;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Allows for hiding the dependencies for configuring Curator.
 */
@Beta
class CuratorInnerModule extends PrivateModule {

    private final Module dependencies;
    private final Class<? extends Annotation> annotation = Curator.class;

    CuratorInnerModule(@Nullable final Module dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    protected void configure() {
        if (dependencies != null) {
            install(dependencies);
        }

        requireBinding(Key.get(EnsembleProvider.class, annotation));
        requireBinding(Key.get(RetryPolicy.class, annotation));

        bind(CuratorFramework.class).annotatedWith(annotation).toProvider(CuratorFrameworkProvider.class)
                .in(Singleton.class);

        bind(CuratorManagementService.class).annotatedWith(annotation).to(DefaultCuratorManagementService.class)
                .in(Singleton.class);

        expose(Key.get(CuratorFramework.class, annotation));
        expose(Key.get(CuratorManagementService.class, annotation));
    }

    @Provides
    public CuratorFrameworkFactory.Builder builder() {
        return CuratorFrameworkFactory.builder();
    }
}
