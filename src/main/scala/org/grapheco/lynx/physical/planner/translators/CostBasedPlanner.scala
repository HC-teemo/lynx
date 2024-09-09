package org.grapheco.lynx.physical.planner.translators

import org.grapheco.lynx.logical.plans.{GraphPattern, GraphPatternEdge, GraphPatternMatch, GraphPatternNode}
import org.grapheco.lynx.physical.PhysicalPlannerContext
import org.grapheco.lynx.physical.plans.{AllNodes, Filter, NodesPlanFactory, PhysicalPlan, PhysicalPlanBuffer, RelationshipsPlanFactory}

import scala.collection.mutable

class CostBasedPlanner()(implicit ppc: PhysicalPlannerContext ){
  private val records: mutable.Map[Set[GraphPatternNode], Candidate] = mutable.HashMap.empty
  //动态规划
  def plan(graph: GraphPattern): Unit = {
    //获取单个节点最优计划
    val nodes = graph.allNodes.toSet
    nodes.foreach { node =>
      records.put(Set(node), nodePlan(node))
    }
    //println(records)
    //获取最优连接顺序
    for (i <- 2 to nodes.size) {
      nodes.subsets(i).foreach { set =>
        val joinPlan = mergePlans(set)
        if (joinPlan.plan!=null) {
          records.put(set,joinPlan)
        }
      }
    }
//    print(records(nodes))
    //println(records)
  }

  //单个节点最优计划
  private def nodePlan(node: GraphPatternNode): Candidate = {
    val factory = NodesPlanFactory(node.variableName.get)
    val localCandidate: Set[PhysicalPlan] = Set.empty +
      // C1: allNodes
       PhysicalPlanBuffer(factory.allNodes())
        .andThen(Filter(node.properties.get)).plan
      // C2: nodeScanByLabel
      // C3: nodeSeekByIndex
      // C4: nodeSeekById
    // ...
    localCandidate.map(CostCalculator.cost).minBy(_.cost)
  }

  private def triplePlan(start: GraphPatternNode, rel: GraphPatternEdge, end: GraphPatternNode): Candidate = {
    /*
    For a triple (a)-[b]-(c), has 3 types of candidate:
    1. Relationship(b)->Filter(a-b-c)
    2. Node(a)->Expand(a-b-c)
    3. Node(c)->Expand(a-b-c)
     */
    val factory = RelationshipsPlanFactory(rel.variableName.get)
    val localCandidate: Set[Candidate] = Set.empty
    // ... TODO
    localCandidate.minBy(_.cost)
  }

  def mergePlans(nodeSet: Set[GraphPatternNode]): Candidate= {
    /*
    For a complex graph (a)-b-(c)-d-(e)-f-(g), has some types of candidate:
    1. ExpandOne: {(a)-b-(c)-d-(e)}->Expand(e->g)
    2. JOIN: {(a)-b-(c)}, {(e)-f-(g)} by (c)-d-(e)
    3. ... maybe other
     */
    val localCandidate: Set[Candidate] = Set.empty
    // ... TODO
    localCandidate.minBy(_.cost)
  }
}
