package org.waterprinciple.cultivar;

import java.util.concurrent.ThreadFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
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

    private final CuratorFrameworkFactory.Builder builder;
    private Optional<TracerDriver> tracerDriver = Optional.absent();

    @Inject
    public CuratorFrameworkProvider(final CuratorFrameworkFactory.Builder builder) {
        this.builder = builder;
    }

    @Inject
    public void setEnsembleProvider(@Curator final EnsembleProvider ensembleProvider) {

        this.builder.ensembleProvider(ensembleProvider);

    }

    @Inject
    public void setRetryPolicy(@Curator final RetryPolicy retryPolicy) {
        this.builder.retryPolicy(retryPolicy);

    }

    @Inject(optional = true)
    public void setConnectionTimeoutMs(
            @Named("Cultivar.Curator.connectionTimeoutMs") @Nonnegative final int connectionTimeoutMs) {

        this.builder.connectionTimeoutMs(connectionTimeoutMs);
    }

    @Inject(optional = true)
    public void setACLProvider(@Curator final ACLProvider aclProvider) {

        this.builder.aclProvider(aclProvider);

    }

    @Inject(optional = true)
    public void setAuthorization(@Named("Cultivar.Curator.auth.scheme") @Nullable final String scheme,
            @Named("Cultivar.Curator.auth.auth") @Nullable final byte[] auth) {

        this.builder.authorization(scheme, auth);

    }

    @Inject(optional = true)
    public void setCompressionProvider(@Curator final CompressionProvider compressionProvider) {

        this.builder.compressionProvider(compressionProvider);

    }

    @Inject(optional = true)
    public void setDefaultData(@Named("Cultivar.Curator.defaultData") final byte[] defaultData) {

        this.builder.defaultData(defaultData);

    }

    @Inject(optional = true)
    public void setNamespace(@Named("Cultivar.Curator.baseNamespace") @Nullable final String namespace) {

        this.builder.namespace(namespace);

    }

    @Inject(optional = true)
    public void setSessionTimeoutMs(@Named("Cultivar.Curator.sessionTimeoutMs") @Nonnegative final int sessionTimeoutMs) {

        this.builder.sessionTimeoutMs(sessionTimeoutMs);

    }

    @Inject(optional = true)
    public void setCanBeReadOnly(@Named("Cultivar.Curator.canBeReadOnly") final boolean canBeReadOnly) {

        this.builder.canBeReadOnly(canBeReadOnly);

    }

    @Inject(optional = true)
    public void setThreadFactory(@Curator final ThreadFactory threadFactory) {

        this.builder.threadFactory(threadFactory);

    }

    @Inject(optional = true)
    public void setZookeeperFactory(@Curator final ZookeeperFactory zookeeperFactory) {
        this.builder.zookeeperFactory(zookeeperFactory);

    }

    @Inject(optional = true)
    public void setTracerDriver(@Curator final TracerDriver tracerDriver) {

        this.tracerDriver = Optional.of(tracerDriver);

    }

    @Override
    public CuratorFramework get() {

        CuratorFramework retval;

        retval = builder.build();

        if (tracerDriver.isPresent()) {
            retval.getZookeeperClient().setTracerDriver(tracerDriver.get());
        }

        return retval;
    }
}
