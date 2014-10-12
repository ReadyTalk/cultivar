package com.readytalk.cultivar.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.readytalk.cultivar.internal.Private;

class NodeCacheProvider implements Provider<NodeCache> {

    private final CuratorFramework client;
    private final String path;
    private final boolean compressedData;

    @Inject
    NodeCacheProvider(@Private final CuratorFramework client, @Named("Cultivar.cache.path") final String path,
            @Named("Cultivar.cache.compressed") final boolean compressedData) {
        this.client = client;
        this.path = path;
        this.compressedData = compressedData;
    }

    @Override
    public NodeCache get() {
        return new NodeCache(client, path, compressedData);
    }
}
