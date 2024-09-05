package org.grapheco.lynx.physical.planner.translators

import org.grapheco.lynx.physical.plans.{AllNodes, NodeSeekByID, NodeSeekByIndex, PhysicalPlan}

case class Candidate(var plan: PhysicalPlan, var cost:BigDecimal, var cardinal:Long)

object CostCalculator {
  val _factor: Map[Class[PhysicalPlan], Float] = Map(
    // nodes
    AllNodes -> 1,
    NodeSeekByID -> 1,
    NodeSeekByIndex -> 1,
  )

  def cost(plan: PhysicalPlan): Candidate = {
    // TODO
    Candidate(plan, 1, 1)
  }
}
