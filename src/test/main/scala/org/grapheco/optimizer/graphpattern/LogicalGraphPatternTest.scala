package org.grapheco.optimizer.graphpattern

import org.grapheco.lynx.TestBase
import org.junit.jupiter.api.Test

class LogicalGraphPatternTest extends TestBase{
  @Test
  def singleNodeMatch(): Unit = {
    runOnDemoGraph(
      """
        |MATCH (p:Person) RETURN p
        |""".stripMargin)
  }
}
