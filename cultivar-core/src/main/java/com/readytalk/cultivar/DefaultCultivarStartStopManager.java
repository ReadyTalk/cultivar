package com.readytalk.cultivar;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.ServiceManager;

@ThreadSafe
@Beta
class DefaultCultivarStartStopManager extends AbstractIdleService implements CultivarStartStopManager {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCultivarStartStopManager.class);

    private final CuratorManagementService curatorManagementService;
    private final ServiceManager serviceManager;

    @Inject
    DefaultCultivarStartStopManager(@Curator final CuratorManagementService curatorManagementService,
            @Cultivar final ServiceManager serviceManager) {
        this.curatorManagementService = curatorManagementService;
        this.serviceManager = serviceManager;

    }

    @Override
    protected void startUp() throws Exception {
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

        serviceManager.startAsync().awaitHealthy();

    }

    @Override
    protected void shutDown() throws Exception {
        try {
            serviceManager.stopAsync().awaitStopped();
        } catch (Exception ex) {
            LOG.warn("Exception when shutting down service manager", ex);
        }

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
