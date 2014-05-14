package org.waterprinciple.cultivar.locks;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnegative;

import com.google.inject.Key;
import com.google.inject.Module;

public interface ReaperBuilder {

    ReaperBuilder exectuor(Key<? extends ScheduledExecutorService> serviceKey);

    ReaperBuilder reapingThreshold(@Nonnegative int time, TimeUnit unit);

    ReaperBuilder leaderPath(String path);

    Module build();
}
