package com.readytalk.cultivar.locks;

import java.lang.annotation.Annotation;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.Reaper;
import com.readytalk.cultivar.Curator;
import com.readytalk.cultivar.CuratorService;
import com.readytalk.cultivar.internal.AnnotationHolder;
import com.readytalk.cultivar.internal.Private;

import com.google.common.annotations.Beta;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

@NotThreadSafe
@Beta
public class ReaperModuleBuilder extends AbstractReaperModuleBuilder<ReaperModuleBuilder> {

    private final AnnotationHolder holder;

    ReaperModuleBuilder(final AnnotationHolder holder) {
        this.holder = holder;
    }

    public static ReaperModuleBuilder create() {
        return new ReaperModuleBuilder(AnnotationHolder.create(Curator.class));
    }

    public static ReaperModuleBuilder create(final Annotation ann) {
        return new ReaperModuleBuilder(AnnotationHolder.create(ann));
    }

    public static ReaperModuleBuilder create(final Class<? extends Annotation> ann) {
        return new ReaperModuleBuilder(AnnotationHolder.create(ann));
    }

    @Override
    public Module build() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                if (getService() != null) {
                    requireBinding(getService());
                }

                final Key<ReaperManager> managerKey = holder.generateKey(ReaperManager.class);

                install(new PrivateModule() {
                    @Override
                    protected void configure() {
                        if (getService() != null) {
                            bind(ScheduledExecutorService.class).annotatedWith(Private.class).to(getService());
                        } else {
                            bind(ScheduledExecutorService.class).annotatedWith(Private.class).toInstance(
                                    Reaper.newExecutorService());
                        }

                        Key<Reaper> reaperKey = holder.generateKey(Reaper.class);

                        bind(reaperKey).to(Key.get(Reaper.class, Private.class)).in(Singleton.class);

                        expose(reaperKey);

                        bind(managerKey).to(ReaperManager.class).in(Singleton.class);

                        expose(managerKey);
                    }

                    @Provides
                    @Private
                    public Reaper reaper(@Curator final CuratorFramework framework,
                            @Private final ScheduledExecutorService executorService) {
                        if (getLeaderPath() != null) {
                            return new Reaper(framework, executorService, getReapingThresholdMillis(), getLeaderPath());
                        } else {
                            return new Reaper(framework, executorService, getReapingThresholdMillis());
                        }
                    }
                });

                Multibinder<CuratorService> serviceMultibinder = Multibinder.newSetBinder(binder(),
                        CuratorService.class);

                serviceMultibinder.addBinding().to(managerKey);

            }
        };
    }
}
