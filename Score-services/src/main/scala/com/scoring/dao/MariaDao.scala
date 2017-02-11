package com.scoring.dao

import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator
import org.skife.jdbi.v2.sqlobject.customizers.Define
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper

/**
 * @author himanshu
 * 
 * This is a DAO class to access data objects from Maria/MySql databases.
 * This class uses standard JDBI to query on data.
 * extends: AbstractDao  
 */

@UseStringTemplate3StatementLocator
abstract class MariaDao() extends AbstractDao{

  @SqlQuery("SELECT * FROM <resourceName>;")
  def getList(@Define("resourceName") resourceName: String): java.util.List[Integer]

}