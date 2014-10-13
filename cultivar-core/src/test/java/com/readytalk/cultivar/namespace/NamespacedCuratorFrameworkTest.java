package com.readytalk.cultivar.namespace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.Watcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
public class NamespacedCuratorFrameworkTest {

    private static final String NAMESPACE = "foo";

    @Mock
    private CuratorFramework framework;

    @Mock
    private CuratorFramework namespacedFramework;

    @Mock
    private Watcher watcher;

    private NamespacedCuratorFramework namespacedDelegateFramework;

    @Before
    public void setUp() throws Exception {
        when(framework.usingNamespace(anyString())).thenReturn(namespacedFramework);

        namespacedDelegateFramework = new NamespacedCuratorFramework(framework, NAMESPACE);
    }

    @Test
    public void start_IgnoresNamespace() {
        namespacedDelegateFramework.start();

        verify(framework).start();
        verifyZeroInteractions(namespacedFramework);
    }

    @Test
    public void usingNamespace_WithPath_ReturnsNewNamespacedFramework() {
        CuratorFramework newFramework = namespacedDelegateFramework.usingNamespace("bar");

        assertEquals("bar", ((NamespacedCuratorFramework) newFramework).getDelegateNamespace());
    }

    @Test
    public void isStarted_IgnoresNamespace() {
        namespacedDelegateFramework.isStarted();

        verify(framework).isStarted();

        verifyZeroInteractions(namespacedFramework);
    }

    @Test
    public void blockUntilConnected_NoArgs_IgnoresNamespace() throws Exception {
        namespacedDelegateFramework.blockUntilConnected();

        verify(framework).blockUntilConnected();
        verifyZeroInteractions(namespacedFramework);
    }

    @Test
    public void blockUntilConnected_WithArgs_IgnoresNamespace() throws Exception {
        namespacedDelegateFramework.blockUntilConnected(1000, TimeUnit.MILLISECONDS);

        verify(framework).blockUntilConnected(1000, TimeUnit.MILLISECONDS);
        verifyZeroInteractions(namespacedFramework);
    }

    @Test
    public void inTransaction_DelegatesToNamespace() {
        namespacedDelegateFramework.inTransaction();

        verify(namespacedFramework).inTransaction();
    }

    @Test
    public void sync_NoArgs_DelegatesToNamespace() {
        namespacedDelegateFramework.sync();

        verify(namespacedFramework).sync();
    }

    @Test
    public void getNamespace_DelegatesToNamespace() {
        namespacedDelegateFramework.getNamespace();

        verify(namespacedFramework).getNamespace();
    }

    @Test
    public void close_IgnoresNamespace() {
        namespacedDelegateFramework.close();

        verify(framework).close();

        verifyZeroInteractions(namespacedFramework);
    }

    @Test
    public void setACL_DelegatesToNamespace() {
        namespacedDelegateFramework.setACL();

        verify(namespacedFramework).setACL();
    }

    @Test
    public void sync_WithPath_DelegatesToNamespace() {
        namespacedDelegateFramework.sync("any", new Object());

        verify(namespacedFramework).sync(anyString(), anyObject());
    }

    @Test
    public void getACL_DelegatesToNamespace() {
        namespacedDelegateFramework.getACL();

        verify(namespacedFramework).getACL();
    }

    @Test
    public void getConnectionStateListenable_IgnoresNamespace() {
        namespacedDelegateFramework.getConnectionStateListenable();

        verify(framework).getConnectionStateListenable();

        verifyZeroInteractions(namespacedFramework);
    }

    @Test
    public void getChildren_DelegatesToNamespace() {
        namespacedDelegateFramework.getChildren();

        verify(namespacedFramework).getChildren();
    }

    @Test
    public void getUnhandledErrorListenable_IgnoresNamespace() {
        namespacedDelegateFramework.getConnectionStateListenable();

        verify(framework).getConnectionStateListenable();

        verifyZeroInteractions(namespacedFramework);
    }

    @Test
    public void getZookeeperClient_DelegatesToNamespace() {
        namespacedDelegateFramework.getZookeeperClient();

        verify(namespacedFramework).getZookeeperClient();
    }

    @Test
    public void newNamespaceAwareEnsurePath_DelegatesToNamespace() {
        namespacedDelegateFramework.newNamespaceAwareEnsurePath("any");

        verify(namespacedFramework).newNamespaceAwareEnsurePath(anyString());
    }

    @Test
    public void getData_DelegatesToNamespace() {
        namespacedDelegateFramework.getData();

        verify(namespacedFramework).getData();
    }

    @Test
    public void delete_DelegatesToNamespace() {
        namespacedDelegateFramework.delete();

        verify(namespacedFramework).delete();
    }

    @Test
    public void setData_DelegatesToNamespace() {
        namespacedDelegateFramework.setData();

        verify(namespacedFramework).setData();
    }

    @Test
    public void getCuratorListenable_IgnoresNamespace() {
        namespacedDelegateFramework.getConnectionStateListenable();

        verify(framework).getConnectionStateListenable();

        verifyZeroInteractions(namespacedFramework);
    }

    @Test
    public void checkExists_DelegatesToNamespace() {
        namespacedDelegateFramework.checkExists();

        verify(namespacedFramework).checkExists();
    }

    @Test
    public void nonNamespaceView_ReturnsNewNamespacedFrameworkWithNullPath() {
        CuratorFramework newFramework = namespacedDelegateFramework.nonNamespaceView();

        assertNull(((NamespacedCuratorFramework) newFramework).getDelegateNamespace());
    }

    @Test
    public void create_DelegatesToNamespace() {
        namespacedDelegateFramework.create();

        verify(namespacedFramework).create();
    }

    @Test
    public void clearWatcherReferences_DelegatesToNamespace() {
        namespacedDelegateFramework.clearWatcherReferences(watcher);

        verify(namespacedFramework).clearWatcherReferences(watcher);
    }

}
