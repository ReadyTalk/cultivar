package org.waterprinciple.cultivar.test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.curator.test.TestingCluster;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;

@Beta
public abstract class AbstractZookeeperClusterTest {

    protected static final int CLUSTER_SIZE = 3;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractZookeeperClusterTest.class);

    private static final long MAX_TEST_TIME_SECONDS = 30L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractZookeeperClusterTest.class);

    @Rule
    public final ExternalResource clusterSetup = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            super.before();

            testingCluster.start();

            LOG.info("{}-servers started with connection string: {}", testingCluster.getServers().size(),
                    testingCluster.getConnectString());
        }

        @Override
        protected void after() {
            super.after();

            try {
                LOG.info("Tearing Down Cluster.");
                testingCluster.close();
            } catch (IOException ex) {
                LOGGER.warn("Exception shutting down cluster.", ex);
            }
        }
    };

    @Rule
    public final Timeout timeout = new Timeout((int) TimeUnit.SECONDS.toMillis(MAX_TEST_TIME_SECONDS));

    protected final TestingCluster testingCluster = new TestingCluster(CLUSTER_SIZE);
}
