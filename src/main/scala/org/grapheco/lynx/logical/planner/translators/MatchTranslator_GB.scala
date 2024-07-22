package org.grapheco.lynx.logical.planner.translators

import org.grapheco.lynx.logical.LogicalPlannerContext
import org.grapheco.lynx.logical.planner.LogicalTranslator
import org.grapheco.lynx.logical.plans.{GraphPattern, GraphPatternEdge, GraphPatternMatch, GraphPatternNode, LogicalPlan}
import org.opencypher.v9_0.ast.{Match, Where}
import org.opencypher.v9_0.expressions.{EveryPath, NamedPatternPart, NodePattern, Pattern, PatternElement, PatternPart, RelationshipChain, RelationshipPattern, ShortestPaths}
import org.grapheco.lynx.logical.plans.ASTConvertor._

import scala.language.implicitConversions

class MatchTranslator_GB(m: Match) extends LogicalTranslator {

  override def translate(in: Option[LogicalPlan])(implicit plannerContext: LogicalPlannerContext): LogicalPlan = {
    val graphPattern: GraphPattern = in match {
      case Some(value: GraphPatternMatch) => value.graphPattern
      case _ => new GraphPattern
    }
    val Match(optional, Pattern(patternParts: Seq[PatternPart]), hints, where: Option[Where]) = m
    patternParts.foreach{
      case EveryPath(element) => translatePattern(element, optional)(graphPattern)
      case ShortestPaths(element, single) => None
//      case NamedPatternPart(variable, patternPart) => translatePattern(element, optional)(graphPattern)
    }
    GraphPatternMatch(graphPattern)
  }

  private def translatePattern(element: PatternElement, optional: Boolean)(graphPattern: GraphPattern): Unit = element match {
      //match ()
      case n:NodePattern => graphPattern.addNode(n)
      //match ()-[]->()
      case RelationshipChain(s: NodePattern, r: RelationshipPattern, t: NodePattern) => graphPattern.addEdge(s,r,t)
      //match ()-[]->()-...-[]->()
      case rc@RelationshipChain(leftChain: RelationshipChain, r: RelationshipPattern, t: NodePattern) =>
        translatePattern(leftChain, optional)(graphPattern)
        graphPattern.addEdge(leftChain.rightNode,r,t)
    }

}
