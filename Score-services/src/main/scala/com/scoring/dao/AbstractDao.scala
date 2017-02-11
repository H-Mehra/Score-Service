package com.scoring.dao


/**
 * @author himanshu
 * Abstract trait to ensure consistency among all DAOs.
 * 
 */
trait AbstractDao {
  def getList(resourceName: String): java.util.List[Integer]
}