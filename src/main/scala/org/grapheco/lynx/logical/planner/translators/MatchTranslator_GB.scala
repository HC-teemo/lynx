package org.grapheco.lynx.logical.planner.translators

import org.grapheco.lynx.logical.LogicalPlannerContext
import org.grapheco.lynx.logical.planner.LogicalTranslator
import org.grapheco.lynx.logical.plans.{GraphPattern, GraphPatternEdge, GraphPatternMatch, GraphPatternNode, LogicalPlan}
import org.opencypher.v9_0.ast.{Match, Where}
import org.opencypher.v9_0.expressions.{AnonymousPatternPart, EveryPath, NamedPatternPart, NodePattern, Pattern, PatternElement, PatternPart, RelationshipChain, RelationshipPattern, ShortestPaths}
import org.grapheco.lynx.logical.plans.ASTConvertor._

import scala.language.implicitConversions

case class MatchTranslator_GB(m: Match) extends LogicalTranslator {

  override def translate(in: Option[LogicalPlan])(implicit plannerContext: LogicalPlannerContext): LogicalPlan = {
    val graphPattern: GraphPattern = in match {
      case Some(value: GraphPatternMatch) => value.graphPattern
      case _ => new GraphPattern
    }
    val Match(optional, Pattern(patternParts: Seq[PatternPart]), hints, where: Option[Where]) = m
    patternParts.foreach{
      case EveryPath(element) => translatePattern(None, element, optional)(graphPattern)
      case ShortestPaths(element, single) => None //TODO graph pattern not support shortest paths
      case NamedPatternPart(variable, patternPart) => patternPart match {
        case EveryPath(element) => translatePattern(Option(variable.name), element, optional)(graphPattern)
        case ShortestPaths(element, single) => None //TODO graph pattern not support shortest paths
      }
    }
    GraphPatternMatch(graphPattern)
  }

  private def translatePattern(variableName: Option[String], element: PatternElement, optional: Boolean)(graphPattern: GraphPattern): Unit = element match {
    // TODO variable name for relationship chain
      //match ()
      case n:NodePattern => graphPattern.addNode(n.withVariableName(variableName))
      //match ()-[]->()
      case RelationshipChain(s: NodePattern, r: RelationshipPattern, t: NodePattern) => graphPattern.addEdge(s,r,t)
      //match ()-[]->()-...-[]->()
      case rc@RelationshipChain(leftChain: RelationshipChain, r: RelationshipPattern, t: NodePattern) =>
        translatePattern(None, leftChain, optional)(graphPattern)
        graphPattern.addEdge(leftChain.rightNode,r,t)
    }
}
