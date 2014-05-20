package com.readytalk.cultivar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ThreadFactory;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.drivers.TracerDriver;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.api.CompressionProvider;
import org.apache.curator.utils.ZookeeperFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CuratorFrameworkProviderTest {

    @Mock
    private CuratorFramework framework;

    @Mock
    private CuratorZookeeperClient zookeeperClient;

    @Mock
    private CuratorFrameworkFactory.Builder builder;

    @Mock
    private EnsembleProvider ensembleProvider;

    @Mock
    private RetryPolicy retryPolicy;

    @Mock
    private ACLProvider aclProvider;

    @Mock
    private CompressionProvider compressionProvider;

    @Mock
    private ThreadFactory threadFactory;

    @Mock
    private ZookeeperFactory zookeeperFactory;

    @Mock
    private TracerDriver tracerDriver;

    private CuratorFrameworkProvider provider;

    @Before
    public void setUp() {
        provider = new CuratorFrameworkProvider(builder);

        when(builder.build()).thenReturn(framework);
        when(framework.getZookeeperClient()).thenReturn(zookeeperClient);
    }

    @Test
    public void setEnsembleProvider_Delegates() {
        provider.setEnsembleProvider(ensembleProvider);

        verify(builder).ensembleProvider(ensembleProvider);

    }

    @Test
    public void setRetryPolicy_Delegates() {
        provider.setRetryPolicy(retryPolicy);

        verify(builder).retryPolicy(retryPolicy);
    }

    @Test
    public void setConnectionTimeoutMs_Delegates() {
        int value = 100;

        provider.setConnectionTimeoutMs(value);

        verify(builder).connectionTimeoutMs(value);
    }

    @Test
    public void setACLProvider_Delegates() {
        provider.setACLProvider(aclProvider);

        verify(builder).aclProvider(aclProvider);
    }

    @Test
    public void setAuthorization_Delegates() {
        byte[] value = new byte[] { 0x02 };

        provider.setAuthorization("", value);

        verify(builder).authorization("", value);
    }

    @Test
    public void setCompressionProvider_Delegates() {
        provider.setCompressionProvider(compressionProvider);

        verify(builder).compressionProvider(compressionProvider);
    }

    @Test
    public void setDefaultData_Delegates() {
        byte[] value = new byte[] { 0x02 };

        provider.setDefaultData(value);

        verify(builder).defaultData(value);
    }

    @Test
    public void setNamespace_Delegates() {
        String value = "namespace";

        provider.setNamespace(value);

        verify(builder).namespace(value);
    }

    @Test
    public void setSessionTimeoutMs_Delegates() {
        int value = 100;

        provider.setSessionTimeoutMs(value);

        verify(builder).sessionTimeoutMs(value);
    }

    @Test
    public void setCanBeReadOnly_Delegates() {
        provider.setCanBeReadOnly(true);

        verify(builder).canBeReadOnly(true);
    }

    @Test
    public void setThreadFactory_Delegates() {
        provider.setThreadFactory(threadFactory);

        verify(builder).threadFactory(threadFactory);
    }

    @Test
    public void setZookeeperFactory_Delegates() {
        provider.setZookeeperFactory(zookeeperFactory);

        verify(builder).zookeeperFactory(zookeeperFactory);
    }

    @Test
    public void get_BuildsFromBuilder() {
        assertEquals(framework, provider.get());

        verify(builder).build();
    }

    @Test
    public void setTracerDriver_AfterGet_SetsOnZookeeperClient() {
        provider.setTracerDriver(tracerDriver);

        provider.get();

        verify(zookeeperClient).setTracerDriver(tracerDriver);
    }

}
