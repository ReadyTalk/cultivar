package org.waterprinciple.cultivar;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.curator.RetryPolicy;
import org.apache.curator.drivers.TracerDriver;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.api.CompressionProvider;
import org.apache.curator.utils.ZookeeperFactory;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * Wraps a CuratorFrameworkFactory.Builder instance and uses setter injection to construct the instance.
 */
@Beta
@ThreadSafe
class CuratorFrameworkProvider implements Provider<CuratorFramework> {

    private final ReadWriteLock builderLock = new ReentrantReadWriteLock();

    private final Lock readLock = builderLock.readLock();
    private final Lock writeLock = builderLock.writeLock();

    @GuardedBy("builderLock")
    private final CuratorFrameworkFactory.Builder builder;

    @GuardedBy("builderLock")
    private Optional<TracerDriver> tracerDriver = Optional.absent();

    @Inject
    public CuratorFrameworkProvider(final CuratorFrameworkFactory.Builder builder) {
        this.builder = builder;
    }

    @Inject
    public void setEnsembleProvider(@Curator final EnsembleProvider ensembleProvider) {

        writeLock.lock();
        try {
            this.builder.ensembleProvider(ensembleProvider);
        } finally {
            writeLock.unlock();
        }
    }

    @Inject
    public void setRetryPolicy(@Curator final RetryPolicy retryPolicy) {
        writeLock.lock();
        try {
            this.builder.retryPolicy(retryPolicy);
        } finally {
            writeLock.unlock();
        }
    }

    @Inject(optional = true)
    public void setConnectionTimeoutMs(
            @Named("Cultivar.Curator.connectionTimeoutMs") @Nonnegative final int connectionTimeoutMs) {
        writeLock.lock();
        try {
            this.builder.connectionTimeoutMs(connectionTimeoutMs);
        } finally {
            writeLock.unlock();
        }
    }

    @Inject(optional = true)
    public void setACLProvider(@Curator final ACLProvider aclProvider) {
        writeLock.lock();
        try {
            this.builder.aclProvider(aclProvider);
        } finally {
            writeLock.lock();
        }
    }

    @Inject(optional = true)
    public void setAuthorization(@Named("Cultivar.Curator.auth.scheme") @Nullable final String scheme,
            @Named("Cultivar.Curator.auth.auth") @Nullable final byte[] auth) {
        writeLock.lock();
        try {
            this.builder.authorization(scheme, auth);
        } finally {
            writeLock.unlock();
        }

    }

    @Inject(optional = true)
    public void setCompressionProvider(@Curator final CompressionProvider compressionProvider) {
        writeLock.lock();
        try {
            this.builder.compressionProvider(compressionProvider);
        } finally {
            writeLock.unlock();
        }

    }

    @Inject(optional = true)
    public void setDefaultData(@Named("Cultivar.Curator.defaultData") final byte[] defaultData) {
        writeLock.lock();
        try {
            this.builder.defaultData(defaultData);
        } finally {
            writeLock.unlock();
        }

    }

    @Inject(optional = true)
    public void setNamespace(@Named("Cultivar.Curator.baseNamespace") @Nullable final String namespace) {
        writeLock.lock();
        try {
            this.builder.namespace(namespace);
        } finally {
            writeLock.unlock();
        }

    }

    @Inject(optional = true)
    public void setSessionTimeoutMs(@Named("Cultivar.Curator.sessionTimeoutMs") @Nonnegative final int sessionTimeoutMs) {
        writeLock.lock();
        try {
            this.builder.sessionTimeoutMs(sessionTimeoutMs);
        } finally {
            writeLock.unlock();
        }

    }

    @Inject(optional = true)
    public void setCanBeReadOnly(@Named("Cultivar.Curator.canBeReadOnly") final boolean canBeReadOnly) {
        writeLock.lock();
        try {
            this.builder.canBeReadOnly(canBeReadOnly);
        } finally {
            writeLock.unlock();
        }

    }

    @Inject(optional = true)
    public void setThreadFactory(@Curator final ThreadFactory threadFactory) {
        writeLock.lock();
        try {
            this.builder.threadFactory(threadFactory);
        } finally {
            writeLock.unlock();
        }

    }

    @Inject(optional = true)
    public void setZookeeperFactory(@Curator final ZookeeperFactory zookeeperFactory) {
        writeLock.lock();
        try {
            this.builder.zookeeperFactory(zookeeperFactory);
        } finally {
            writeLock.unlock();
        }

    }

    @Inject(optional = true)
    public void setTracerDriver(@Curator final TracerDriver tracerDriver) {
        writeLock.lock();
        try {
            this.tracerDriver = Optional.of(tracerDriver);
        } finally {
            writeLock.unlock();
        }

    }

    @Override
    public CuratorFramework get() {

        CuratorFramework retval;

        readLock.lock();
        try {
            retval = builder.build();

            if (tracerDriver.isPresent()) {
                retval.getZookeeperClient().setTracerDriver(tracerDriver.get());
            }
        } finally {
            readLock.unlock();
        }

        return retval;
    }
}
