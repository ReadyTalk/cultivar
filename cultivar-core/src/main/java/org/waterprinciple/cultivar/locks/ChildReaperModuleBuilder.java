package org.waterprinciple.cultivar.locks;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.Annotation;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.ChildReaper;
import org.apache.curator.framework.recipes.locks.Reaper;
import org.waterprinciple.cultivar.Curator;
import org.waterprinciple.cultivar.CuratorService;
import org.waterprinciple.cultivar.internal.AnnotationHolder;
import org.waterprinciple.cultivar.internal.Private;

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
public class ChildReaperModuleBuilder extends AbstractReaperModuleBuilder<ChildReaperModuleBuilder> {

    private final AnnotationHolder holder;

    private String lockPath = null;

    private Reaper.Mode mode = null;

    ChildReaperModuleBuilder(final AnnotationHolder holder) {
        this.holder = holder;
    }

    public static ChildReaperModuleBuilder create() {
        return new ChildReaperModuleBuilder(AnnotationHolder.create(Curator.class));
    }

    public static ChildReaperModuleBuilder create(final Annotation ann) {
        return new ChildReaperModuleBuilder(AnnotationHolder.create(ann));
    }

    public static ChildReaperModuleBuilder create(final Class<? extends Annotation> ann) {
        return new ChildReaperModuleBuilder(AnnotationHolder.create(ann));
    }

    public ChildReaperModuleBuilder lockPath(final String path) {
        this.lockPath = checkNotNull(path);

        return this;
    }

    public ChildReaperModuleBuilder reaperMode(final Reaper.Mode reaperMode) {
        this.mode = checkNotNull(reaperMode);

        return this;
    }

    @Override
    public Module build() {
        checkState(lockPath != null, "Lock path must be provided.");
        checkState(mode != null, "Reaper mode must be provided.");

        return new AbstractModule() {
            @Override
            protected void configure() {
                if (getService() != null) {
                    requireBinding(getService());
                }

                final Key<ChildReaperManager> managerKey = holder.generateKey(ChildReaperManager.class);

                install(new PrivateModule() {
                    @Override
                    protected void configure() {
                        if (getService() != null) {
                            bind(ScheduledExecutorService.class).annotatedWith(Private.class).to(getService());
                        } else {
                            bind(ScheduledExecutorService.class).annotatedWith(Private.class).toInstance(
                                    Reaper.newExecutorService());
                        }

                        Key<ChildReaper> reaperKey = holder.generateKey(ChildReaper.class);

                        bind(reaperKey).to(Key.get(ChildReaper.class, Private.class)).in(Singleton.class);

                        expose(reaperKey);

                        bind(managerKey).to(ChildReaperManager.class).in(Singleton.class);

                        expose(managerKey);
                    }

                    @Provides
                    @Private
                    public ChildReaper childReaper(@Curator final CuratorFramework framework,
                            @Private final ScheduledExecutorService executorService) {
                        if (getLeaderPath() != null) {
                            return new ChildReaper(framework, lockPath, mode, executorService,
                                    getReapingThresholdMillis(), getLeaderPath());
                        } else {
                            return new ChildReaper(framework, lockPath, mode, executorService,
                                    getReapingThresholdMillis());
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
