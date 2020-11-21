/*
 *
 *                                No Copyright
 *                      
 */

package com.xite.api.db

import com.xite.api.{ DatabaseLogin, DatabasePassword, DatabaseUrl }

/**
  * A base for our database migrator.
  *
  * @tparam F A higher kinded type which wraps the actual return value.
  */
trait DatabaseMigrator[F[_]] {

  /**
    * Apply pending migrations to the database.
    *
    * @param url  A JDBC database connection url.
    * @param user The login name for the connection.
    * @param pass The password for the connection.
    * @return The number of applied migrations.
    */
  def migrate(url: DatabaseUrl, user: DatabaseLogin, pass: DatabasePassword): F[Int]

}
