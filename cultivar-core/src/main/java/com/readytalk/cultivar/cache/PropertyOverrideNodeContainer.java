package com.readytalk.cultivar.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;
import com.readytalk.cultivar.CuratorService;
import com.readytalk.cultivar.internal.Private;
import com.readytalk.cultivar.util.PropertyReader;
import com.readytalk.cultivar.util.mapping.ByteArrayMapper;

/**
 * A variation on the NodeContainer that is designed for testing and for rapid prototyping of systems that will
 * eventually move to ZK, but aren't full connected yet. Treats a property as the source and checks it every second
 * (configurable via config.cultivar.nodecache.property.millis) to accurately represent listener changes.
 *
 *
 */
@ThreadSafe
public class PropertyOverrideNodeContainer<T> extends AbstractScheduledService implements CuratorService,
        NodeContainer<T> {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyOverrideNodeContainer.class);

    private final long propertyCheckRateMillis = Long.parseLong(PropertyReader.getProperty(
            "config.cultivar.nodecache.property.millis", "1000"));

    private final ConcurrentHashMap<NodeCacheListener, Executor> listeners = new ConcurrentHashMap<>();

    private final ByteArrayMapper<T> mapper;
    private final String propOverride;

    private String lastValue = null;

    @Inject
    PropertyOverrideNodeContainer(@Private final ByteArrayMapper<T> mapper, @Private final Optional<String> propOverride) {
        this.mapper = mapper;
        this.propOverride = propOverride.get();
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(propertyCheckRateMillis, propertyCheckRateMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void runOneIteration() {
        String newValue = PropertyReader.getProperty(propOverride);

        if (!Objects.equal(newValue, lastValue)) {
            for (final Map.Entry<NodeCacheListener, Executor> o : listeners.entrySet()) {
                o.getValue().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            o.getKey().nodeChanged();
                        } catch (Exception ex) {
                            LOG.warn("Exception when processing node change.", ex);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void startUp() throws Exception {
        lastValue = PropertyReader.getProperty(propOverride);
    }

    @Override
    protected void shutDown() throws Exception {

    }

    @Nullable
    @Override
    public T get() {
        String prop = PropertyReader.getProperty(propOverride);

        if (prop != null) {
            return mapper.map(prop.getBytes(Charsets.UTF_8));
        } else {
            return null;
        }
    }

    @Override
    public T get(final T def) {
        return MoreObjects.firstNonNull(this.get(), def);
    }

    @Override
    public void addListener(final NodeCacheListener listener) {
        this.addListener(listener, MoreExecutors.directExecutor());
    }

    @Override
    public void addListener(final NodeCacheListener listener, final Executor executor) {
        listeners.put(listener, executor);
    }

    @Override
    public void rebuild() {
        this.runOneIteration();
    }
}
