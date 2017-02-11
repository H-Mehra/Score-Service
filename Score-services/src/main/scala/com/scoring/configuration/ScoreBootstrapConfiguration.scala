package com.scoring.configuration

import com.fasterxml.jackson.annotation.JsonProperty

import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory
import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * @author himanshu
 * 
 * ScoreBootstrapConfiguration is the primary configuration for BootStrapApplication.
 * This configuration holds default Database connection configuration for JDBI 
 * and other application related configurations. These configurations objects are 
 * instantiated with the data in .yml file passed as argument.        
 *
 */
class ScoreBootstrapConfiguration extends Configuration {
  
  @Valid
  @NotNull
  @JsonProperty
  val dataSourceFactory: DataSourceFactory = new DataSourceFactory();

  val scoreConfiguration = new ScoreConfiguration()

}