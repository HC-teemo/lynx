package org.grapheco.lynx.physical.planner.translators

import org.grapheco.lynx.types.structural.LynxNodeLabel

object MetaData {
  val nodeNum: Int = Int.MaxValue
  val labelNum:Map[LynxNodeLabel,Int]=Map(
    LynxNodeLabel("Person")->100
  )
}
