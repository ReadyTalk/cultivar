package com.readytalk.cultivar.test.scala

import java.lang
import java.util.concurrent.Callable

import com.readytalk.cultivar.test.ConditionalWait


object ZKTestUtil {

  import scala.concurrent.duration._

  private val defaultPollInterval = ConditionalWait.DEFAULT_AWAIT_POLL_MILLIS.milliseconds

  def conditionalWait(f: () => Boolean, pollInterval: Duration = defaultPollInterval) = {
    new ConditionalWait(new Callable[lang.Boolean] {
      override def call(): lang.Boolean = f()
    }, pollInterval.length, pollInterval.unit)
  }
}
