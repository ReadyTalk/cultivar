package org.waterprinciple.cultivar.locks;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.apache.curator.framework.recipes.locks.Reaper;
import org.waterprinciple.cultivar.CuratorService;
import org.waterprinciple.cultivar.internal.Private;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.AbstractIdleService;

@ThreadSafe
@Beta
public class ReaperManager extends AbstractIdleService implements CuratorService {

    private final Reaper reaper;

    @Inject
    ReaperManager(@Private final Reaper reaper) {
        this.reaper = reaper;
    }

    @Override
    protected void startUp() throws Exception {
        reaper.start();

    }

    @Override
    protected void shutDown() throws IOException {
        reaper.close();

    }
}
