package com.scoring.configuration

import org.hibernate.validator.constraints.NotEmpty

/**
 * @author himanshu
 *
 * ScoreConfiguration is the placeholder for general application related configuration details.
 */
class ScoreConfiguration {
  @NotEmpty val refreshFreqInMinutes = 5
  
  @NotEmpty val HotelRUleDefaultScore = 5
  
  @NotEmpty val CountryRuleDefaultScore = 3
  
}