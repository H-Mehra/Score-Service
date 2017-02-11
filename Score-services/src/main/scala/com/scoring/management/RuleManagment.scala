package com.scoring.management

import com.scoring.dto.RuleDto
import com.scoring.dto.ScoreResponseDto
import com.scoring.dto.ScoreRequestDto

trait RuleManagment {
  def getFinalScore(reqDto: ScoreRequestDto): ScoreResponseDto
  
  def updateRuleScore(ruleId: String, newScore: Double): Option[RuleDto]
  
  def enableRule(ruleId: String, enable: Boolean): Option[RuleDto]
  
  def ruleDump(): List[RuleDto]
}