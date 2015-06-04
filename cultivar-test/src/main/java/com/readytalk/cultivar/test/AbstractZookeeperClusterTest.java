package com.readytalk.cultivar.test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.curator.test.TestingCluster;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;
import com.google.common.base.Stopwatch;

@Beta
public abstract class AbstractZookeeperClusterTest {

    protected static final int CLUSTER_SIZE = 3;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractZookeeperClusterTest.class);

    private static final long MAX_TEST_TIME_SECONDS = 120L;

    @Rule
    public final ExternalResource clusterSetup = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            super.before();

            Stopwatch stopwatch = Stopwatch.createStarted();

            LOG.info("Attempting to start ZK cluster: {}", testingCluster.getConnectString());

            testingCluster.start();

            LOG.info("{}-servers started in {} milliseconds with connection string: {}", testingCluster.getServers()
                    .size(), stopwatch.stop().elapsed(TimeUnit.MILLISECONDS), testingCluster.getConnectString());
        }

        @Override
        protected void after() {
            super.after();

            try {
                LOG.info("Tearing Down Cluster.");
                testingCluster.close();
            } catch (IOException ex) {
                LOG.warn("Exception shutting down cluster.", ex);
            }
        }
    };

    @Rule
    public final Timeout timeout;

    protected final TestingCluster testingCluster;

    public AbstractZookeeperClusterTest() {
        this(CLUSTER_SIZE, MAX_TEST_TIME_SECONDS, TimeUnit.SECONDS);
    }

    public AbstractZookeeperClusterTest(final int clusterSize, final long testTime, final TimeUnit unit) {

        timeout = new Timeout(testTime, unit);
        testingCluster = new TestingCluster(clusterSize);
    }
}
