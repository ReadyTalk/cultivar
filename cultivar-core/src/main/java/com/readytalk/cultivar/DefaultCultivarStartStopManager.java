package com.readytalk.cultivar;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.Inject;
import com.readytalk.cultivar.management.ShutdownManager;

@ThreadSafe
@Beta
class DefaultCultivarStartStopManager extends AbstractIdleService implements CultivarStartStopManager {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCultivarStartStopManager.class);

    private final CuratorManagementService curatorManagementService;
    private final ServiceManager serviceManager;
    private final Set<ServiceManager> additionalManagers;

    private volatile ShutdownManager manager = null;

    @Inject
    DefaultCultivarStartStopManager(@Curator final CuratorManagementService curatorManagementService,
            @Cultivar final ServiceManager serviceManager, @Cultivar final Set<ServiceManager> additionalManagers) {
        this.curatorManagementService = curatorManagementService;
        this.serviceManager = serviceManager;
        this.additionalManagers = additionalManagers;

    }

    @Inject(optional = true)
    public void setShutdownManager(final ShutdownManager shutdownManager) {
        this.manager = shutdownManager;
    }

    @Override
    protected void startUp() throws Exception {

        LOG.debug("Starting up management service {}", curatorManagementService);

        try {
            curatorManagementService.startAsync().awaitRunning();
        } catch (IllegalStateException ex) {
            Throwable cause;

            try {
                cause = curatorManagementService.failureCause();
            } catch (IllegalStateException ex2) {
                throw Throwables.propagate(ex);
            }

            Throwables.propagateIfPossible(cause, Exception.class);
            throw Throwables.propagate(cause);
        }

        LOG.debug("Starting up main ServiceManager {}", serviceManager);

        serviceManager.startAsync().awaitHealthy();

        LOG.debug("Starting up plugin ServiceManagers {}", additionalManagers);

        Set<ServiceManager> successes = Sets.newHashSet();

        for (ServiceManager o : additionalManagers) {
            try {
                successes.add(o.startAsync());
            } catch (IllegalStateException ex) {
                LOG.warn("Exception with starting a ServiceManager {}", o, ex);
            }
        }

        for (ServiceManager o : successes) {
            try {
                o.awaitHealthy();
            } catch (IllegalStateException ex) {
                LOG.warn("Exception in starting a service in ServiceManager {}", o, ex);
            }
        }

        ShutdownHook.register(this, manager);
    }

    @Override
    protected void shutDown() throws Exception {

        LOG.debug("Shutting down plugin ServiceManagers {}", additionalManagers);

        Set<ServiceManager> successes = Sets.newHashSet();

        for (ServiceManager o : additionalManagers) {
            try {
                successes.add(o.stopAsync());
            } catch (IllegalStateException ex) {
                LOG.warn("Exception with stopping a ServiceManager {}", o, ex);
            }
        }

        for (ServiceManager o : successes) {
            try {
                o.awaitStopped();
            } catch (IllegalStateException ex) {
                LOG.warn("Exception in stopping a service in ServiceManager {}", o, ex);
            }
        }

        LOG.debug("Shutting down main ServiceManager {}", serviceManager);

        try {
            serviceManager.stopAsync().awaitStopped();
        } catch (Exception ex) {
            LOG.warn("Exception when shutting down service manager", ex);
        }

        LOG.debug("Shutting down management service {}", curatorManagementService);

        try {
            curatorManagementService.stopAsync().awaitTerminated();
        } catch (IllegalStateException ex) {
            Throwable cause;
            try {
                cause = curatorManagementService.failureCause();
            } catch (IllegalStateException ex2) {
                throw Throwables.propagate(ex);
            }

            Throwables.propagateIfPossible(cause, Exception.class);
            throw Throwables.propagate(cause);
        }

    }
}
