package org.waterprinciple.cultivar.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.retry.RetryNTimes;
import org.junit.Test;

public class ZookeeperClusterTest extends AbstractZookeeperClusterTest {

    @Test
    public void checkExists_PathCreated_ReturnsNonNullResult() throws Exception {
        CuratorFramework framework = CuratorFrameworkFactory.newClient(testingCluster.getConnectString(),
                new RetryNTimes(3, 1000));

        framework.start();

        try {
            framework.create().forPath("/testpath");

            assertNotNull("Path '/testpath' does not exist.", framework.checkExists().forPath("/testpath"));
        } finally {
            framework.close();
        }
    }

    @Test
    public void getServers_Size_EqualToClusterSize() {
        assertEquals(CLUSTER_SIZE, testingCluster.getServers().size());
    }

}
