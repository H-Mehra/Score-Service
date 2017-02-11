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

/**
 *
 *
 */
class ScoreTests extends FlatSpec with Matchers {

  var dbi: Option[DBI] = None
  var dao: Option[AbstractDao] = None

  "JDBI" should "be able to establish a connection with database" in {
    val environment = new Environment("test-env", Jackson.newObjectMapper(), null, new MetricRegistry(), null)
    val dataSourceFactory = new DataSourceFactory();
    dataSourceFactory.setDriverClass("com.mysql.jdbc.Driver");
    dataSourceFactory.setUrl("jdbc:mysql://mysql.server:3306/score");
    dataSourceFactory.setUser("root");
    dataSourceFactory.setPassword("root");
    try {
      val tdbi = new DBIFactory().build(environment, dataSourceFactory, "test")
      tdbi.open()
      dbi = Some(tdbi)
      succeed
    } catch {
      case e: Exception => fail("JDBI failed to establish a connection with database")
    }
  }

  "DBI factory" should "be able to instantiate MariaDao class" in {
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

  "Dao" should "be able to query the database and return the expected results" in {
    dbi match {
      case None => cancel("Test cancelled becacuse JDBI was not able to establish a connection")
      case Some(dbi) => {
        dao match {
          case None => cancel("Test cancelled because DBI  factory was not able to instantiate Dao class")
          case Some(mariaDao) => {
            try {
              val resultList = mariaDao.getList("testTable")
              resultList shouldBe a[java.util.List[_]]
              assert(resultList.contains(21342))
            } catch {
              case e: Exception => fail("Dao failed to query data and retrieve results")
            }
          }
        }
      }
    }
  }
  
  "hotelList and countryList" should "be available in database" in {
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

}