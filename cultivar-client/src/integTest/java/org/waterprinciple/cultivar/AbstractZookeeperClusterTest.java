package org.waterprinciple.cultivar;

import java.io.IOException;

import org.apache.curator.test.TestingCluster;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractZookeeperClusterTest {
    private static final int CLUSTER_SIZE = 3;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractZookeeperClusterTest.class);

    protected final TestingCluster testingCluster = new TestingCluster(CLUSTER_SIZE);

    @Rule
    public final ExternalResource clusterSetup = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            super.before();

            testingCluster.start();
        }

        @Override
        protected void after() {
            super.after();

            try {
                testingCluster.close();
            } catch (IOException ex) {
                LOGGER.warn("Exception shutting down cluster.", ex);
            }
        }
    };
}
