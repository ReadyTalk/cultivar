package com.readytalk.cultivar.namespace;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.GetACLBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.SetACLBuilder;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.curator.framework.api.SyncBuilder;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.Watcher;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.readytalk.cultivar.internal.Private;

/**
 * Circumvents a few (surprising) restrictions in the CuratorFramework around namespaces at the cost of some efficiency.
 * This may have side effects if you are expecting the usingNamespace version of the CuratorFramework, but strives to
 * reduce the level of surprise for those whose experience is using the CuratorFramework directly.
 *
 * <ul>
 * <li>The CuratorFrameworkImpl requires that the CuratorFramework be started before usingNamespace is called.</li>
 * <li>Some methods–such as the listeners–are not callable on the namespaced version of the framework but can be called
 * on the original.</li>
 * <li>Tries to apply usingNamespace selectively on those methods that need it, while simply forwarding more general
 * methods that can be called on a un-started client directly to the original CuratorFramework.</li>
 * </ul>
 */
@Beta
public class NamespacedCuratorFramework implements CuratorFramework {

    private final CuratorFramework delegateFramework;
    private final String namespace;

    @Inject
    NamespacedCuratorFramework(@Private final CuratorFramework delegateFramework,
            @Private @Nullable final String namespace) {
        this.delegateFramework = delegateFramework;
        this.namespace = namespace;
    }

    CuratorFramework namespaceDelegate() {
        return this.delegateFramework.usingNamespace(namespace);
    }

    @VisibleForTesting
    String getDelegateNamespace() {
        return namespace;
    }

    @Override
    public void start() {
        delegateFramework.start();
    }

    @Override
    public CuratorFramework usingNamespace(@Nullable final String newNamespace) {
        return new NamespacedCuratorFramework(this.delegateFramework, newNamespace);
    }

    @Override
    @Deprecated
    public boolean isStarted() {
        return delegateFramework.isStarted();
    }

    @Override
    public boolean blockUntilConnected(final int maxWaitTime, @Nullable final TimeUnit units)
            throws InterruptedException {
        return delegateFramework.blockUntilConnected(maxWaitTime, units);
    }

    @Override
    public CuratorFrameworkState getState() {
        return delegateFramework.getState();
    }

    @Override
    public CuratorTransaction inTransaction() {
        return namespaceDelegate().inTransaction();
    }

    @Override
    public SyncBuilder sync() {
        return namespaceDelegate().sync();
    }

    @Override
    public String getNamespace() {
        return namespaceDelegate().getNamespace();
    }

    @Override
    public void close() {
        delegateFramework.close();
    }

    @Override
    public SetACLBuilder setACL() {
        return namespaceDelegate().setACL();
    }

    @Deprecated
    @Override
    public void sync(final String path, final Object backgroundContextObject) {
        namespaceDelegate().sync(path, backgroundContextObject);
    }

    @Override
    public GetACLBuilder getACL() {
        return namespaceDelegate().getACL();
    }

    @Override
    public Listenable<ConnectionStateListener> getConnectionStateListenable() {
        return delegateFramework.getConnectionStateListenable();
    }

    @Override
    public GetChildrenBuilder getChildren() {
        return namespaceDelegate().getChildren();
    }

    @Override
    public Listenable<UnhandledErrorListener> getUnhandledErrorListenable() {
        return delegateFramework.getUnhandledErrorListenable();
    }

    @Override
    public CuratorZookeeperClient getZookeeperClient() {
        return namespaceDelegate().getZookeeperClient();
    }

    @Override
    public EnsurePath newNamespaceAwareEnsurePath(final String path) {
        return namespaceDelegate().newNamespaceAwareEnsurePath(path);
    }

    @Override
    public GetDataBuilder getData() {
        return namespaceDelegate().getData();
    }

    @Override
    public DeleteBuilder delete() {
        return namespaceDelegate().delete();
    }

    @Override
    public SetDataBuilder setData() {
        return namespaceDelegate().setData();
    }

    @Override
    public Listenable<CuratorListener> getCuratorListenable() {
        return delegateFramework.getCuratorListenable();
    }

    @Override
    public ExistsBuilder checkExists() {
        return namespaceDelegate().checkExists();
    }

    @Override
    @Deprecated
    public CuratorFramework nonNamespaceView() {
        return this.usingNamespace(null);
    }

    @Override
    public CreateBuilder create() {
        return namespaceDelegate().create();
    }

    @Override
    public void blockUntilConnected() throws InterruptedException {
        delegateFramework.blockUntilConnected();
    }

    @Override
    public void clearWatcherReferences(final Watcher watcher) {
        namespaceDelegate().clearWatcherReferences(watcher);
    }
}
