package org.grapheco.lynx.physical.planner

import org.grapheco.lynx.logical.plans._
import org.grapheco.lynx.physical._
import org.grapheco.lynx.physical.planner.translators.{GraphPatternMatchTranslator, LPTShortestPathTranslator, PPTCreateTranslator, PPTMergeTranslator, PPTPatternMatchTranslator, PPTRemoveTranslator, PPTSetClauseTranslator, PPTUnwindTranslator}
import org.grapheco.lynx.physical.plans.{Aggregation, Apply, CreateIndex, CreateUnit, Cross, Delete, Distinct, DropIndex, Filter, Join, Limit, OrderBy, PhysicalPlan, ProcedureCall, Project, Select, Skip, Union, With}
import org.grapheco.lynx.runner.CypherRunnerContext
import org.opencypher.v9_0.expressions._

/**
 * @ClassName DefaultPhysicalPlanner
 * @Description
 * @Author Hu Chuan
 * @Date 2022/4/27
 * @Version 0.1
 */
class DefaultPhysicalPlanner(runnerContext: CypherRunnerContext) extends PhysicalPlanner {
  override def plan(logicalPlan: LogicalPlan)(implicit plannerContext: PhysicalPlannerContext): PhysicalPlan = {
    implicit val runnerContext: CypherRunnerContext = plannerContext.runnerContext
    logicalPlan match {
      case LogicalProcedureCall(procedureNamespace: Namespace, procedureName: ProcedureName, declaredArguments: Option[Seq[Expression]]) =>
        ProcedureCall(procedureNamespace: Namespace, procedureName: ProcedureName, declaredArguments: Option[Seq[Expression]])
      case lc@LogicalCreate(pattern) => PPTCreateTranslator(pattern).translate(lc.in.map(plan(_)))(plannerContext)
      case lm@LogicalMerge(pattern, actions) => PPTMergeTranslator(pattern, actions).translate(lm.in.map(plan(_)))(plannerContext)
//      case lm@LPTMergeAction(m: Seq[MergeAction]) => PPTMergeAction(m)(plan(lm.in.get), plannerContext)
      case ld@LogicalDelete(expressions,forced) => Delete(expressions, forced)
      case ls@LogicalSelect(columns: Seq[(String, Option[String])]) => Select(columns)
      case lp@LogicalProject(ri) => Project(ri)
      case la@LogicalAggregation(a, g) => Aggregation(a, g)
      case lc@LogicalCreateUnit(items) => CreateUnit(items)
      case lf@LogicalFilter(expr) => Filter(expr)
      case lw@LogicalWith(ri) => With(ri)
      case ld@LogicalDistinct() => Distinct()
      case ll@LogicalLimit(expr) => Limit(expr)
      case lo@LogicalOrderBy(sortItem) => OrderBy(sortItem)
      case ll@LogicalSkip(expr) => Skip(expr)
      case lj@LogicalJoin(isSingleMatch, joinType) => Join(None, isSingleMatch, joinType)
      case lc@LogicalCross() => Cross()
      case ap@LogicalAndThen(joinType) => {
        val first = plan(ap.first)
        val contextWithArg: PhysicalPlannerContext = plannerContext.withArgumentsContext(first.schema.map(_._1))
        val andThen = plan(ap._then)(contextWithArg)
        Apply(joinType)(first, andThen, contextWithArg)
      }
      case aj@LogicalAndThenJoin(isSingleMatch, joinType) => {
        val first = plan(aj.first)
        val contextWithArg: PhysicalPlannerContext = plannerContext.withArgumentsContext(first.schema.map(_._1))
        val andThen = plan(aj._then)(contextWithArg)
        Join(None, isSingleMatch, joinType)(first, andThen, contextWithArg)
      }
      case patternMatch: LogicalPatternMatch => PPTPatternMatchTranslator(patternMatch)(plannerContext).translate(None)
      case graphPatternMath: GraphPatternMatch => GraphPatternMatchTranslator(graphPatternMath)(plannerContext).translate(None)
      case lPTShortestPaths : LogicalShortestPaths => LPTShortestPathTranslator(lPTShortestPaths)(plannerContext).translate(None)
      case li@LogicalCreateIndex(labelName: String, properties: List[String]) => CreateIndex(labelName, properties)(plannerContext)
      case li@LogicalDropIndex(labelName: String, properties: List[String]) => DropIndex(labelName, properties)(plannerContext)
      case sc@LogicalSetClause(d) => PPTSetClauseTranslator(d.items).translate(sc.in.map(plan(_)))(plannerContext)
      case lr@LogicalRemove(r) => PPTRemoveTranslator(r.items).translate(lr.in.map(plan(_)))(plannerContext)
      case lu@LogicalUnwind(u) => PPTUnwindTranslator(u.expression, u.variable).translate(lu.in.map(plan(_)))(plannerContext)
      case un@LogicalUnion(distinct) => Union(distinct)(plan(un.a), plan(un.b), plannerContext)
      case _ => throw new Exception("physical plan not support:" +logicalPlan)
    }
  }
}
