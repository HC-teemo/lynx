package org.grapheco.optimizer.graphpattern

import org.grapheco.lynx.TestBase
import org.junit.jupiter.api.Test

class LogicalGraphPatternTest extends TestBase{
  @Test
  def singleAllNodeMatch(): Unit = {
    runOnDemoGraph(
      """
        |MATCH (p) RETURN p
        |""".stripMargin)
  }

  @Test
  def singleNodeByLabelMatch(): Unit = {
    runOnDemoGraph(
      """
        |MATCH (p:Person) RETURN p
        |""".stripMargin)
  }

  @Test
  def singleNodeIndexMatch(): Unit = {
    runOnDemoGraph(
      """
        |MATCH (p:Person{id: "304398046515308"}) RETURN p
        |""".stripMargin)
  }
  //MATCH (p:person{gender: "female"}) RETURN p
  //MATCH (p{gender: "female"}) RETURN p
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
