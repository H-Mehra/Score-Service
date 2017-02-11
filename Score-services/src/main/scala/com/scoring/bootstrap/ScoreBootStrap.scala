package com.scoring.bootstrap

import com.datasift.dropwizard.scala.ScalaApplication
import com.datasift.dropwizard.scala.jdbi.JDBI
import com.scoring.configuration.ScoreBootstrapConfiguration
import com.scoring.dao.MariaDao
import com.scoring.management.RuleManagmentImpl
import com.scoring.services.ScoringSevice
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import javax.ws.rs.Path
import javax.ws.rs.Produces

/**
 * @author himanshu
 *
 * ScoreBootstrap is the Startup module of Score-services.
 * This module handles creation of connection pool with DBs and adds
 * resource bundles to Application Environment.
 *
 */
object ScoreBootStrap extends ScalaApplication[ScoreBootstrapConfiguration] {

  override def init(bootstrap: Bootstrap[ScoreBootstrapConfiguration]): Unit = {

  }

  override def run(conf: ScoreBootstrapConfiguration, enivronment: Environment) = {
    try {
      val dbi = JDBI(enivronment, conf.dataSourceFactory, "MySql")
      val dao = dbi.onDemand(classOf[MariaDao])
      val scoreConf = conf.scoreConfiguration
      enivronment.jersey().register(new ScoringSevice(new RuleManagmentImpl(dao, scoreConf)))
    } catch {
      case e:Exception => e.printStackTrace()
    }
  }

}