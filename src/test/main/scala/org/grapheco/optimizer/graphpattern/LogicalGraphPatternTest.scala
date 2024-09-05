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

  @Test
  def chain(): Unit = {
    runOnDemoGraph(
      """
        |MATCH (p:Person)-[r:knows]->(q:Person) RETURN p
        |""".stripMargin)
  }

  @Test
  def multiChain(): Unit = {
    runOnDemoGraph(
      """
        |MATCH (p:Person)-[r:knows]->(q:Person)
        | MATCH (q:Person)-[r:loves]->(t:Topic)
        |RETURN p
        |""".stripMargin)
  }
}
