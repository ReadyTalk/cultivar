package com.readytalk.cultivar.discovery;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;

class BlankRegistrationService extends AbstractIdleService implements RegistrationService<Void> {

    @Inject
    BlankRegistrationService() {

    }

    @Override
    protected void startUp() throws Exception {

    }

    @Override
    protected void shutDown() throws Exception {

    }
}
