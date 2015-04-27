package com.readytalk.cultivar.cache;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.listen.ListenerContainer;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.utils.PathUtils;

/**
 * To get around CURATOR-169.
 */
public class NodeCacheWrapper {

    private final CuratorFramework framework;
    private final String path;
    private final boolean dataIsCompressed;

    private volatile NodeCache delegateObj = null;

    @SuppressWarnings("UnusedDeclaration")
    NodeCacheWrapper(final CuratorFramework framework, final String path) {
        this(framework, path, false);
    }

    NodeCacheWrapper(final CuratorFramework framework, final String path, final boolean dataIsCompressed) {
        this.framework = framework;
        this.path = PathUtils.validatePath(path);
        this.dataIsCompressed = dataIsCompressed;
    }

    private NodeCache delegate() {
        if (delegateObj == null) {
            synchronized (this) {
                delegateObj = new NodeCache(framework, path, dataIsCompressed);
            }
        }

        return delegateObj;
    }

    public void start() throws Exception {
        delegate().start();
    }

    public void start(final boolean buildInitial) throws Exception {
        delegate().start(buildInitial);
    }

    public void close() throws IOException {
        delegate().close();
    }

    public ListenerContainer<NodeCacheListener> getListenable() {
        return delegate().getListenable();
    }

    public void rebuild() throws Exception {
        delegate().rebuild();
    }

    public ChildData getCurrentData() {
        return delegate().getCurrentData();
    }
}
