package com.readytalk.cultivar.cache;

import org.apache.curator.framework.CuratorFramework;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.readytalk.cultivar.internal.Private;

class NodeCacheProvider implements Provider<NodeCacheWrapper> {

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
    public NodeCacheWrapper get() {
        return new NodeCacheWrapper(client, path, compressedData);
    }
}
