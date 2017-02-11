package com.scoring.management

import java.util.UUID
import scala.collection.JavaConversions
import scala.collection.JavaConversions.asScalaBuffer
import org.joda.time.DateTime
import org.joda.time.Minutes
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator
import com.scoring.dto.RuleDto
import com.scoring.configuration.ScoreConfiguration
import com.scoring.dao.MariaDao
import com.scoring.dao.AbstractDao
import com.scoring.dto.ScoreRequestDto
import com.scoring.dto.ScoreResponseDto

/**
 * @author himanshu
 *
 * RuleManagementImpl is a implementation of RuleManagement trait.
 * This class contains Rules logic to calculate score for each user.
 */
class RuleManagmentImpl(dao: AbstractDao, config: ScoreConfiguration) extends RuleManagment {
  private[this] var lastRefresh = DateTime.now()
  private[this] var firstBoot = true
  private[this] var ruleMap: Map[String, RuleDto] = initializeRulesMap(dao, config)

  /**
   * This function calculated the final score for a given pair of hotelId and countryId.
   *
   */
  override def getFinalScore(reqDto: ScoreRequestDto): ScoreResponseDto = {
    if (Minutes.minutesBetween(lastRefresh, DateTime.now).getMinutes > config.refreshFreqInMinutes) ruleMap = reloadRulesResources(dao, ruleMap)
    var scoreList: List[Int] = List[Int]()
    ruleMap.values.foreach { rule =>
      {
        val id = classOf[ScoreRequestDto].getMethod(rule.appliesOn).invoke(reqDto).asInstanceOf[Int]
        rule.enableFlag && rule.ruleResource.contains(id) match {
          case true  => scoreList = rule.score :: scoreList
          case false => scoreList = 0 :: scoreList
        }
      }
    }
    ScoreResponseDto(reqDto.hotelId, scoreList.max)
  }

  /**
   * This function updated the score in the rule for a given ruleId.
   *
   */
  override def updateRuleScore(ruleId: String, newScore: Int): Option[RuleDto] = {
    ruleMap get ruleId match {
      case None       => None
      case Some(rule) => ruleMap = ruleMap + (ruleId -> rule.copy(score = newScore)); Some(ruleMap(ruleId))
    }
  }

  /**
   * This function enables and disables the rule given the ruleId.
   *
   */
  override def enableRule(ruleId: String, flag: Boolean): Option[RuleDto] = {
    ruleMap get ruleId match {
      case None       => None
      case Some(rule) => ruleMap = ruleMap + (ruleId -> rule.copy(enableFlag = flag)); Some(ruleMap(ruleId))
    }
  }

  /**
   * This functions  generates current rules list.
   *
   */
  override def ruleDump: List[RuleDto] = {
    ruleMap.values.toList
  }

  /**
   *
   *
   */
  def reloadRulesResources(dao: AbstractDao, rulesMap: Map[String, RuleDto]) = {
    println("Reloading rules resource. Time : " + DateTime.now())
    val reloadRule = loadRuleResource(dao) _
    rulesMap map {
      case (id, rule) => {
        reloadRule(rule) match {
          case Right(newRule) => (newRule.ruleId -> newRule)
          case Left(oldRule)  => println("Warning: " + oldRule.ruleId + "'s resources could not be updated"); (oldRule.ruleId -> oldRule)
        }
      }
    }
  }

  /**
   * This function is responsible for initializing the rules list.
   * To add a rule initilize the new rule in this function and add the new rule to last Map.
   *
   */
  private[this] def initializeRulesMap(dao: AbstractDao, config: ScoreConfiguration) = {
    val reloadRule = loadRuleResource(dao) _
    var htlRule: RuleDto = null
    var ctrRule: RuleDto = null

    // Initializing hotel rule 
    reloadRule(RuleDto(UUID.randomUUID().toString(), "hotelId", config.HotelRUleDefaultScore, true, "hotelList", List[Int]())) match {
      case Left(htR) =>
        throw new Exception(htR.resourceName + " rule initialization failed. Exiting."); System.exit(1)
      case Right(htR) => htlRule = htR
    }

    // Initializing country rule
    reloadRule(RuleDto(UUID.randomUUID().toString(), "countryId", config.CountryRuleDefaultScore, true, "countryList", List[Int]())) match {
      case Left(ctR) =>
        throw new Exception(ctR.resourceName + " rule initialization failed. Exiting."); System.exit(1)
      case Right(ctR) => ctrRule = ctR
    }

    Map(htlRule.ruleId -> htlRule, ctrRule.ruleId -> ctrRule)
  }

  /**
   *
   *
   */
  def loadRuleResource(dao: AbstractDao)(rule: RuleDto): Either[RuleDto, RuleDto] = {
    try {
      Right(rule.copy(ruleResource = dao.getList(rule.resourceName).toList.map { jInt => jInt.toInt }))
    } catch {
      case e: Exception => {
        println("Error: " + rule.resourceName + " rule resource load failed")
        e.printStackTrace()
        Left(rule)
      }
    }
  }

}
  
  