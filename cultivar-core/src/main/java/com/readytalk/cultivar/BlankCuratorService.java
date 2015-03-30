package com.readytalk.cultivar;

import com.google.common.util.concurrent.AbstractIdleService;

/**
 * A class that does nothing to allow for servicemanager instantiation when nothing is configured.
 */
class BlankCuratorService extends AbstractIdleService implements CuratorService {
    @Override
    protected void startUp() throws Exception {

    }

    @Override
    protected void shutDown() throws Exception {

    }
}
