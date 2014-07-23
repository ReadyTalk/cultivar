package com.readytalk.cultivar.health;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionHealthTest {

    @Mock
    private CuratorFramework framework;

    @Mock
    private Stat path;

    @Mock
    private ExistsBuilder existsBuilder;

    private ConnectionHealth test;

    @Before
    public void setUp() throws Exception {

        when(framework.checkExists()).thenReturn(existsBuilder);

        test = new ConnectionHealth(framework);
    }

    @Test
    public void check_FrameworkIsLatent_ReturnsHealthy() throws Exception {
        when(framework.getState()).thenReturn(CuratorFrameworkState.LATENT);

        assertTrue(test.check().isHealthy());
    }

    @Test
    public void check_FrameworkIsStopped_ReturnsHealthy() throws Exception {
        when(framework.getState()).thenReturn(CuratorFrameworkState.STOPPED);

        assertTrue(test.check().isHealthy());
    }

    @Test
    public void check_FrameworkIsStartedAndRootNamespaceExists_ReturnsHealthy() throws Exception {
        when(framework.getState()).thenReturn(CuratorFrameworkState.STARTED);
        when(existsBuilder.forPath(anyString())).thenReturn(path);

        assertTrue(test.check().isHealthy());
    }

    @Test
    public void check_FrameworkIsStartedAndRootNamespaceDoesNotExist_ReturnsUnHealthy() throws Exception {
        when(framework.getState()).thenReturn(CuratorFrameworkState.STARTED);
        when(existsBuilder.forPath(anyString())).thenReturn(null);

        assertFalse(test.check().isHealthy());
    }
}
