package com.readytalk.cultivar;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.ServiceManager;

@ThreadSafe
@Beta
class DefaultCultivarStartStopManager extends AbstractIdleService implements CultivarStartStopManager {

    private final CuratorManagementService curatorManagementService;
    private final ServiceManager serviceManager;

    @Inject
    DefaultCultivarStartStopManager(@Curator final CuratorManagementService curatorManagementService,
            @Cultivar final ServiceManager serviceManager) {
        this.curatorManagementService = curatorManagementService;
        this.serviceManager = serviceManager;

    }

    @Override
    protected void startUp() {
        curatorManagementService.startAsync().awaitRunning();
        serviceManager.startAsync().awaitHealthy();
    }

    @Override
    protected void shutDown() {
        serviceManager.stopAsync().awaitStopped();
        curatorManagementService.stopAsync().awaitTerminated();

    }
}
