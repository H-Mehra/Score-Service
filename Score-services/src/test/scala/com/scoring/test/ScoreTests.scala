package com.scoring.test

import org.scalatest.FlatSpec
import org.scalatest._
import io.dropwizard.setup.Environment
import io.dropwizard.jackson.Jackson
import com.codahale.metrics.MetricRegistry
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.db.DataSourceFactory
import com.scoring.dao.MariaDao
import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Handle
import com.scoring.dao.AbstractDao
import com.scoring.management.RuleManagment
import com.scoring.management.RuleManagmentImpl
import com.scoring.configuration.ScoreConfiguration
import com.scoring.dto.ScoreRequestDto
import com.scoring.dto.ScoreResponseDto

/**
 *
 *
 */
class ScoreTests extends FlatSpec with Matchers {

  var dbi: Option[DBI] = None
  var dao: Option[AbstractDao] = None
  var mgmt: Option[RuleManagmentImpl] = None
  val sConfig = new ScoreConfiguration()

  "JDBI" should "Be able to establish a connection with database" in {
    val environment = new Environment("test-env", Jackson.newObjectMapper(), null, new MetricRegistry(), null)
    val dataSourceFactory = new DataSourceFactory();
    dataSourceFactory.setDriverClass("com.mysql.jdbc.Driver");
    dataSourceFactory.setUrl("jdbc:mysql://mysql.server:3306/testScore");
    dataSourceFactory.setUser("root");
    dataSourceFactory.setPassword("root");
    try {
      val tdbi = new DBIFactory().build(environment, dataSourceFactory, "Score-test")
      tdbi.open()
      dbi = Some(tdbi)
      succeed
    } catch {
      case e: Exception => fail("JDBI failed to establish a connection with database")
    }
  }

  "DBI factory" should "Be able to instantiate MariaDao class" in {
    dbi match {
      case None => cancel("Test cancelled becacuse JDBI was not able to establish a connection")
      case Some(dbi) => {
        try {
          dao = Some(dbi.onDemand(classOf[MariaDao]))
          dao.get shouldBe a[MariaDao]
        } catch {
          case e: Exception => fail("JDBI failed to establish a connection with database")
        }
      }
    }
  }

  "Dao" should "Be able to query the database and return the expected results" in {
    dbi match {
      case None => cancel("Test cancelled becacuse JDBI was not able to establish a connection")
      case Some(dbi) => {
        dao match {
          case None => cancel("Test cancelled because DBI  factory was not able to instantiate Dao class")
          case Some(mariaDao) => {
            try {
              val resultList = mariaDao.getList("hotelList")
              resultList shouldBe a[java.util.List[_]]
              assert(resultList.contains(23537))
            } catch {
              case e: Exception => fail("Dao failed to query data and retrieve results")
            }
          }
        }
      }
    }
  }

  "hotelList and countryList" should "Be available in test database" in {
    dbi match {
      case None => cancel("Test cancelled becacuse JDBI was not able to establish a connection")
      case Some(dbi) => {
        dao match {
          case None => cancel("Test cancelled because DBI  factory was not able to instantiate Dao class")
          case Some(mariaDao) => {
            try {
              val hotelList = mariaDao.getList("hotelList")
              val countryList = mariaDao.getList("countryList")
              succeed
            } catch {
              case e: Exception => fail("hotelList or countryList is not available in database")
            }
          }
        }
      }
    }
  }

  "Rule Management instance" should "Be able to function independently and perform score operations" in {
    dbi match {
      case None => cancel("Test cancelled becacuse JDBI was not able to establish a connection")
      case Some(dbi) => {
        dao match {
          case None => cancel("Test cancelled because DBI  factory was not able to instantiate Dao class")
          case Some(mariaDao) => {
            try {
              mgmt = Some(new RuleManagmentImpl(dao.get, sConfig))
              succeed
            } catch {
              case e: Exception => fail("Rule Management Could not be instantiated.")
            }
          }
        }
      }
    }
  }

  it should "Return score 5.0 for hotelId=23537&countryId=24253 pair as both \n  ids exists in the testlists and hotelrule-countryrule score are 5.0, 3.0 respectively" in {
    dbi match {
      case None => cancel("Test cancelled becacuse JDBI was not able to establish a connection")
      case Some(dbi) => {
        dao match {
          case None => cancel("Test cancelled because DBI  factory was not able to instantiate Dao class")
          case Some(mariaDao) => {
            mgmt match {
              case None => cancel("Test cancelled because Management Could not be instantiated.")
              case Some(mgmnt) => {
                assertResult(5.0) {
                  mgmnt.getFinalScore(ScoreRequestDto(23537, 24253)).score
                }
              }
            }
          }
        }
      }
    }
  }
  
  it should "Return score 0.0 for hotelId=2357&countryId=2425 pair as both ids are not present in the test lists." in {
    dbi match {
      case None => cancel("Test cancelled becacuse JDBI was not able to establish a connection")
      case Some(dbi) => {
        dao match {
          case None => cancel("Test cancelled because DBI  factory was not able to instantiate Dao class")
          case Some(mariaDao) => {
            mgmt match {
              case None => cancel("Test cancelled because Management Could not be instantiated.")
              case Some(mgmnt) => {
                assertResult(0.0) {
                  mgmnt.getFinalScore(ScoreRequestDto(2357, 2425)).score
                }
              }
            }
          }
        }
      }
    }
  }

  it should "Return score 3.0 for hotelId=2357&countryId=24253 pair as hotelId \n  doesn't exists in test hotelList and hotelrule-countryrule score are 5.0, 3.0 respectively" in {
    dbi match {
      case None => cancel("Test cancelled becacuse JDBI was not able to establish a connection")
      case Some(dbi) => {
        dao match {
          case None => cancel("Test cancelled because DBI  factory was not able to instantiate Dao class")
          case Some(mariaDao) => {
            mgmt match {
              case None => cancel("Test cancelled because Management Could not be instantiated.")
              case Some(mgmnt) => {
                assertResult(3.0) {
                  mgmnt.getFinalScore(ScoreRequestDto(2357, 24253)).score
                }
              }
            }
          }
        }
      }
    }
  }

  it should "Return score 8.0 for hotelId=23537&countryId=24253, if we update \n  the countryRule score from 3.0 to 8.0, as both ids exists in the testlists" in {
    dbi match {
      case None => cancel("Test cancelled becacuse JDBI was not able to establish a connection")
      case Some(dbi) => {
        dao match {
          case None => cancel("Test cancelled because DBI  factory was not able to instantiate Dao class")
          case Some(mariaDao) => {
            mgmt match {
              case None => cancel("Test cancelled because Management Could not be instantiated.")
              case Some(mgmnt) => {
                mgmnt.ruleDump.find(rule => rule.appliesOn == "countryId") match {
                  case None       => fail("Can not find countryRule")
                  case Some(rule) => mgmnt.updateRuleScore(rule.ruleId, 8.0)
                }
                assertResult(8.0) {
                  mgmnt.getFinalScore(ScoreRequestDto(23537, 24253)).score
                }
              }
            }
          }
        }
      }
    }
  }
  
  it should "Return 5.0 for hotelId=23537&countryId=24253 if we disable the cuntryRule, as both ids exists in the testlists" in {
    dbi match {
      case None => cancel("Test cancelled becacuse JDBI was not able to establish a connection")
      case Some(dbi) => {
        dao match {
          case None => cancel("Test cancelled because DBI  factory was not able to instantiate Dao class")
          case Some(mariaDao) => {
            mgmt match {
              case None => cancel("Test cancelled because Management Could not be instantiated.")
              case Some(mgmnt) => {
                mgmnt.ruleDump.find(rule => rule.appliesOn == "countryId") match {
                  case None       => fail("Can not find countryRule")
                  case Some(rule) => mgmnt.enableRule(rule.ruleId, false)
                }
                assertResult(5.0) {
                  mgmnt.getFinalScore(ScoreRequestDto(23537, 24253)).score
                }
              }
            }
          }
        }
      }
    }
  }

}