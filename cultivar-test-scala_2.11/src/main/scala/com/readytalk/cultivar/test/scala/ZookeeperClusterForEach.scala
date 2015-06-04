package com.readytalk.cultivar.test.scala

import java.io.IOException
import java.util.concurrent.TimeUnit

import com.google.common.base.Stopwatch
import com.typesafe.scalalogging.LazyLogging
import org.apache.curator.test.TestingCluster
import org.scalatest.concurrent.TimeLimitedTests
import org.scalatest.time.Span
import org.scalatest.{BeforeAndAfterEach, Suite, SuiteMixin}

import scala.concurrent.duration._

/**
 * Creates a zookeeper cluster fresh for each test, allowing for isolation.
 *
 * Not designed for parallel execution of tests. For that isolated fixtures are required.
 */
trait ZookeeperClusterForEach extends SuiteMixin with BeforeAndAfterEach with LazyLogging with TimeLimitedTests {
  this: Suite =>

  override def timeLimit: Span = 2.minutes

  def clusterSize: Int = 3

  @volatile var cluster: Option[TestingCluster] = Option.empty

  implicit def testingCluster: TestingCluster = cluster.get

  override protected def beforeEach(): Unit = {
    super.beforeEach()

    cluster = Option(new TestingCluster(clusterSize))

    val stopwatch = Stopwatch.createStarted

    logger.info("Attempting to start ZK cluster: {}", testingCluster.getConnectString)

    testingCluster.start()

    logger.info(s"${testingCluster.getServers.size}-servers started in ${
      stopwatch.stop.elapsed(TimeUnit.MILLISECONDS)
    } milliseconds with connection string: ${testingCluster.getConnectString}")

  }

  override protected def afterEach(): Unit = {
    super.afterEach()

    try {
      logger.info("Tearing Down Cluster: {}", testingCluster)
      testingCluster.close()
    } catch {
      case ex: IOException =>
        logger.warn("Exception shutting down cluster.", ex)
    }

    cluster = Option.empty
  }
}
