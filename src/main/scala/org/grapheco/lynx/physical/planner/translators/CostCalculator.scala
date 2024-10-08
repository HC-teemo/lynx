package org.grapheco.lynx.physical.planner.translators

import org.grapheco.lynx.physical.planner.translators.MetaData.{labelNum, nodeNum}
import org.grapheco.lynx.physical.plans.{AllNodes, NodeScan, NodeSeekByID, NodeSeekByIndex, PhysicalPlan}
import org.grapheco.lynx.types.structural.LynxNodeLabel

case class Candidate(var plan: PhysicalPlan, var cost:BigDecimal, var cardinal:Long)

object CostCalculator {
  val _factor: Map[Class[_<:PhysicalPlan], Double] = Map(
    // nodes
    classOf[AllNodes] -> 1,
    classOf[NodeScan] -> 0.2,
    classOf[NodeSeekByID] -> 0.1,
    classOf[NodeSeekByIndex] -> 1,
  )

  def cost(plan: PhysicalPlan): Candidate = plan match{
    // TODO
  case n:AllNodes => Candidate(n, _factor(classOf[AllNodes])*nodeNum, nodeNum)
  case n:NodeScan => Candidate(n, _factor(classOf[NodeScan])*nodeNum, labelNum(n.pattern.labels.head))
  //case _ => throw new UnsupportedOperationException("Unsupported plan type")
  }
}
