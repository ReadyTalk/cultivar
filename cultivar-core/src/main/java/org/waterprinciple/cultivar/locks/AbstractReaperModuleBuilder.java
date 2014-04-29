package org.waterprinciple.cultivar.locks;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.annotations.Beta;
import com.google.inject.Key;

@NotThreadSafe
@Beta
public abstract class AbstractReaperModuleBuilder<T extends AbstractReaperModuleBuilder<T>> implements ReaperBuilder {

    private static final int DEFAULT_REAPING_THRESHOLD_MILLIS = (int) TimeUnit.MILLISECONDS
            .convert(5, TimeUnit.MINUTES);

    private Key<? extends ScheduledExecutorService> service;

    private String leaderPath = null;

    private int reapingThresholdMillis = DEFAULT_REAPING_THRESHOLD_MILLIS;

    @Override
    @SuppressWarnings("unchecked")
    public T exectuor(final Key<? extends ScheduledExecutorService> serviceKey) {
        this.service = checkNotNull(serviceKey);

        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T reapingThreshold(@Nonnegative final int time, final TimeUnit unit) {
        checkArgument(time > 0, "Time must be > 0.");

        reapingThresholdMillis = (int) checkNotNull(unit).toMillis(time);

        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T leaderPath(final String path) {

        this.leaderPath = checkNotNull(path);

        return (T) this;
    }

    protected Key<? extends ScheduledExecutorService> getService() {
        return service;
    }

    protected String getLeaderPath() {
        return leaderPath;
    }

    protected int getReapingThresholdMillis() {
        return reapingThresholdMillis;
    }
}
