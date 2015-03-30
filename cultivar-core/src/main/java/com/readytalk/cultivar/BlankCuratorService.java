package com.readytalk.cultivar;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;

/**
 * A class that does nothing to allow for servicemanager instantiation when nothing is configured.
 */
public class BlankCuratorService extends AbstractIdleService implements CuratorService {

    @Inject
    BlankCuratorService() {

    }

    @Override
    protected void startUp() throws Exception {

    }

    @Override
    protected void shutDown() throws Exception {

    }
}
