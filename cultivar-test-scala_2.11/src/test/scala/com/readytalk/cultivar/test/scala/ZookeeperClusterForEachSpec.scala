package com.readytalk.cultivar.test.scala

import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ZookeeperClusterForEachSpec extends WordSpec with ZookeeperClusterForEach {

  "A ZookeeperClusterForEach" when {
    "there is a test" should {
      "create a zookeeper cluster" in {
        assert(testingCluster !== null)
      }
    }
  }
}
