package com.readytalk.cultivar.cache;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.EnsurePath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NodeCacheProviderTest {

    @Mock
    private CuratorFramework framework;

    @Mock
    private EnsurePath ensurePath;

    private NodeCacheProvider provider;

    @Before
    public void setUp() throws Exception {
        final String path = "/dev/test";

        when(framework.newNamespaceAwareEnsurePath(path)).thenReturn(ensurePath);

        provider = new NodeCacheProvider(framework, path, true);
    }

    @Test
    public void get_ReturnsNonNull() {
        assertNotNull(provider.get());
    }
}
