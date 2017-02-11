package com.scoring.dto

case class RuleDto (ruleId: String, appliesOn: String, score:Double, enableFlag: Boolean, resourceName: String, ruleResource: List[Int])