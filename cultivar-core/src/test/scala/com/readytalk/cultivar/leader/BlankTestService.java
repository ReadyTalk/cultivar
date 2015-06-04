package com.readytalk.cultivar.leader;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class BlankTestService extends AbstractIdleService implements LeaderService {

    private final String value;

    @Inject
    BlankTestService(@Named("dependency") final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    protected void startUp() throws Exception {

    }

    @Override
    protected void shutDown() throws Exception {

    }
}
