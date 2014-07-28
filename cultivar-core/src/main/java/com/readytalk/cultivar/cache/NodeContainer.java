package com.readytalk.cultivar.cache;

import java.util.concurrent.Executor;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.curator.framework.recipes.cache.NodeCacheListener;

import com.readytalk.cultivar.CuratorService;

/**
 * Represents a NodeCache object with some convenience wrapping to facilitate easier value checking. Does not
 * distinguish between a node not being present and it being set to null as a value.
 *
 * Thread safe, but is only guaranteed to be as consistent as the underlying NodeCache.
 */
@ThreadSafe
public interface NodeContainer<T> extends CuratorService {
    /**
     * Retrieve the value of the node.
     * 
     * @return The value of the node, mapped to the type T. Returns null if the node does not exist or if the data is
     *         null.
     */
    @Nullable
    T get();

    /**
     * Retrieve the value of the node or a default.
     * 
     * @return The value of the node, mapped to the type T. Returns def if the node does not exist or if the data is
     *         null.
     */
    T get(T def);

    /**
     * Add a listener to the NodeCache.
     */
    void addListener(NodeCacheListener listener);

    /**
     * Add a listener to the NodeCache to be run with the given executor.
     */
    void addListener(NodeCacheListener listener, Executor executor);

    /**
     * Rebuild the cache, not notifying listeners of changes.
     */
    void rebuild();
}
