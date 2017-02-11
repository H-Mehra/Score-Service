package com.scoring.dto

case class RuleDto (ruleId: String, appliesOn: String, score:Int, enableFlag: Boolean, resourceName: String, ruleResource: List[Int])